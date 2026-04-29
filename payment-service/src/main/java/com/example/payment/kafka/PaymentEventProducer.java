package com.example.payment.kafka;

import com.example.payment.event.PaymentConfirmedEvent;
import com.example.payment.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendConfirmed(PaymentConfirmedEvent event) {
        kafkaTemplate.send("payment.confirmed", String.valueOf(event.bookingId()), event);
    }

    public void sendFailed(PaymentFailedEvent event) {
        kafkaTemplate.send("payment.failed", String.valueOf(event.bookingId()), event);
    }
}
