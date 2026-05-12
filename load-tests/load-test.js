import http from 'k6/http';
import {check, sleep} from 'k6';
import {Trend, Rate} from 'k6/metrics';

const sagaCompletionTime = new Trend('saga_completion_time_ms');
const kafkaProducerSuccess = new Rate('kafka_producer_success');
const sagaConfirmedSuccess = new Rate('saga_confirmed_success');

export const options = {
    scenarios: {
        throughput_test: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,
            maxVUs: 100,
            exec: 'testProducerThroughput'
        },
        latency_test: {
            executor: 'shared-iterations',
            vus: 10,
            iterations: 50,
            maxDuration: '1m',
            exec: 'testSagaLatency',
            startTime: '35s'
        }
    }
};

const BASE_URL = 'http://localhost:8081';

export function testProducerThroughput() {
    const payload = JSON.stringify({
        customerId: `C-${__VU}-${__ITER}`,
        shipId: 'SH001',
        containerCount: Math.floor(Math.random() * 3) + 1
    });

    const res = http.post(`${BASE_URL}/bookings`, payload, {
        headers: {'Content-Type': 'application/json'}
    });

    kafkaProducerSuccess.add(res.status === 201);
}

export function testSagaLatency() {
    const startTime = Date.now();
    const payload = JSON.stringify({
        customerId: `C-${__VU}-${__ITER}`,
        shipId: 'SH001',
        containerCount: 1
    });

    const createRes = http.post(`${BASE_URL}/bookings`, payload, {
        headers: {'Content-Type': 'application/json'}
    });

    if (createRes.status === 201) {
        const bookingId = createRes.json('id');
        let isCompleted = false;
        let attempts = 0;

        while (!isCompleted && attempts < 300) {
            sleep(0.1);
            attempts++;

            const getRes = http.get(`${BASE_URL}/bookings/${bookingId}`);
            if (getRes.status === 200) {
                const status = getRes.json('status');

                if (status === 'CONFIRMED' || status === 'CANCELLED') {
                    isCompleted = true;
                    sagaCompletionTime.add(Date.now() - startTime);
                    sagaConfirmedSuccess.add(status === 'CONFIRMED');
                }
            }
        }

        check(isCompleted, {'Saga completed successfully': (v) => v === true});
    }
}
