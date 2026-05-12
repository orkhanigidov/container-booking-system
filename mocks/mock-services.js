const dns = require('dns');
const defaultLookup = dns.lookup;

dns.lookup = (hostname, options, callback) => {
    let cb = callback;
    let opts = options;
    if (typeof options === 'function') {
        cb = options;
        opts = {};
    }

    if (hostname === 'kafka') {
        return defaultLookup('127.0.0.1', opts, cb);
    }
    return defaultLookup(hostname, opts, cb);
};

const {Kafka} = require('kafkajs');

const kafka = new Kafka({
    clientId: 'mock-microservices',
    brokers: ['localhost:9092']
});

const consumer = kafka.consumer({groupId: 'mock-group'});
const producer = kafka.producer();

async function run() {
    await producer.connect();
    await consumer.connect();

    await consumer.subscribe({topics: ['booking.created', 'inventory.reserved'], fromBeginning: false});

    console.log('Mock Services running. Listening for booking.created and inventory.reserved...');

    await consumer.run({
        eachMessage: async ({topic, message}) => {
            const eventData = JSON.parse(message.value.toString());
            const bookingId = eventData.bookingId;

            const messageKey = message.key ? message.key.toString() : String(bookingId);

            const delay = Math.floor(Math.random() * 500) + 200;

            await new Promise(resolve => setTimeout(resolve, delay));

            if (topic === 'booking.created') {
                console.log(`[INVENTORY] Received booking.created for bookingId: ${bookingId}`);

                const rand = Math.random();

                if (rand < 0.90) {
                    // Success
                    await producer.send({
                        topic: 'inventory.reserved',
                        messages: [{
                            key: messageKey,
                            value: JSON.stringify({
                                bookingId: bookingId,
                                shipId: eventData.shipId,
                                containerCount: eventData.containerCount
                            })
                        }]
                    });
                    console.log(`[INVENTORY] Sent inventory.reserved for bookingId: ${bookingId}`);
                } else {
                    // Inventory failure
                    await producer.send({
                        topic: 'inventory.failed',
                        messages: [{
                            key: messageKey,
                            value: JSON.stringify({bookingId: bookingId, reason: "No containers available"})
                        }]
                    });
                    console.log(`[INVENTORY] Sent inventory.failed for bookingId: ${bookingId}`);
                }
            } else if (topic === 'inventory.reserved') {
                console.log(`[PAYMENT] Received inventory.reserved for bookingId: ${bookingId}`);

                const rand = Math.random();

                if (rand < 0.89) {
                    await producer.send({
                        topic: 'payment.confirmed',
                        messages: [{
                            key: messageKey,
                            value: JSON.stringify({bookingId: bookingId})
                        }]
                    });
                    console.log(`[PAYMENT] Sent payment.confirmed for bookingId: ${bookingId}`);
                } else {
                    // Payment failure
                    await producer.send({
                        topic: 'payment.failed',
                        messages: [{
                            key: messageKey,
                            value: JSON.stringify({
                                bookingId: bookingId,
                                shipId: eventData.shipId,
                                containerCount: eventData.containerCount,
                                reason: "Card declined"
                            })
                        }]
                    });
                    console.log(`[PAYMENT] Sent payment.failed for bookingId: ${bookingId}`);
                }
            }
        }
    });
}

run().catch(console.error);
