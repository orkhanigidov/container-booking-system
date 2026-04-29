package com.example.notification.kafka;

import com.example.notification.event.PaymentConfirmedEvent;
import com.example.notification.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "payment.confirmed", groupId = "notification-service")
    public void onPaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("Preparing to send confirmation email for bookingId={}", event.bookingId());

        var messageBody = """
                Thank you for your booking!
                Your container booking #%d has been successfully confirmed.
                """.formatted(event.bookingId());
        sendEmail("customer@example.com", "Booking Confirmed: #" + event.bookingId(), messageBody);
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.warn("Preparing to send cancellation email for bookingId={}. Reason: {}", event.bookingId(), event.reason());

        var messageBody = """
                We are sorry, but your booking #d was cancelled.
                Reason: %s
                """.formatted(event.bookingId(), event.reason());
        sendEmail("customer@example.com", "Booking Cancelled: #" + event.bookingId(), messageBody);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@booking-system.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("=== EMAIL SENT SUCCESSFULLY to {} ===", to);
        } catch (Exception e) {
            log.error("=== FAILED TO SEND EMAIL to {}: {} ===", to, e.getMessage());
        }
    }
}
