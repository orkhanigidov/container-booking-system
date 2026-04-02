package com.example.notification.kafka;

import com.example.notification.event.PaymentConfirmedEvent;
import com.example.notification.event.PaymentFailedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationConsumerTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    void onPaymentConfirmed_shouldSendConfirmationEmail() {
        PaymentConfirmedEvent event = new PaymentConfirmedEvent();
        event.setBookingId(101L);

        notificationConsumer.onPaymentConfirmed(event);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly("customer@example.com");
        assertThat(sentMessage.getFrom()).isEqualTo("noreply@booking-system.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Booking Confirmed: #101");
        assertThat(sentMessage.getText()).contains("successfully confirmed");
    }

    @Test
    void onPaymentFailed_shouldSendCancellationEmail() {
        PaymentFailedEvent event = new PaymentFailedEvent();
        event.setBookingId(202L);
        event.setReason("Insufficient funds");

        notificationConsumer.onPaymentFailed(event);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly("customer@example.com");
        assertThat(sentMessage.getFrom()).isEqualTo("noreply@booking-system.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Booking Cancelled: #202");
        assertThat(sentMessage.getText()).contains("Insufficient funds");
    }
}
