package com.example.notification.kafka;

import com.example.notification.event.PaymentConfirmedEvent;
import com.example.notification.event.PaymentFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(topics = "payment.confirmed", groupId = "notification-service")
    public void onPaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("=== EMAIL SENT === Booking #{} CONFIRMED. Thank you for your booking!",
                event.getBookingId());
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.warn("=== EMAIL SENT === Booking #{} CANCELLED. Reason: {}",
                event.getBookingId(), event.getReason());
    }
}
