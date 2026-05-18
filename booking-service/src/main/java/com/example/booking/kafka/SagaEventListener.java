package com.example.booking.kafka;

import com.example.booking.event.InventoryFailedEvent;
import com.example.booking.event.InventoryReleaseEvent;
import com.example.booking.event.PaymentConfirmedEvent;
import com.example.booking.event.PaymentFailedEvent;
import com.example.booking.model.BookingStatus;
import com.example.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventListener {

    private final BookingService bookingService;
    private final BookingEventProducer producer;

    @KafkaListener(topics = KafkaTopics.INVENTORY_FAILED, groupId = "booking-service")
    public void onInventoryFailed(InventoryFailedEvent event) {
        log.warn("Inventory failed for bookingId={}. Cancelling.", event.bookingId());
        bookingService.updateStatus(event.bookingId(), BookingStatus.CANCELLED);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_CONFIRMED, groupId = "booking-service")
    public void onPaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("Payment confirmed for bookingId={}. Booking is CONFIRMED.", event.bookingId());
        bookingService.updateStatus(event.bookingId(), BookingStatus.CONFIRMED);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "booking-service")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.warn("Payment failed for bookingId={}. Starting compensation.", event.bookingId());
        bookingService.updateStatus(event.bookingId(), BookingStatus.CANCELLED);
        producer.sendInventoryRelease(new InventoryReleaseEvent(event.bookingId(), event.shipId(), event.containerCount()));
    }
}
