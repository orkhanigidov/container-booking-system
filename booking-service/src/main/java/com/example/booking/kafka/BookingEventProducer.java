package com.example.booking.kafka;

import com.example.booking.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private static final String TOPIC = "booking.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookingCreated(BookingCreatedEvent event) {
        log.info("Sending booking.created event for bookingId={}", event.bookingId());
        kafkaTemplate.send(TOPIC, String.valueOf(event.bookingId()), event);
    }
}
