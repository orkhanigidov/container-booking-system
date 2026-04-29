package com.example.inventory.kafka;

import com.example.inventory.event.InventoryFailedEvent;
import com.example.inventory.event.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendReserved(InventoryReservedEvent event) {
        log.info("Sending inventory.reserved for bookingId={}", event.bookingId());
        kafkaTemplate.send("inventory.reserved", String.valueOf(event.bookingId()), event);
    }

    public void sendFailed(InventoryFailedEvent event) {
        log.info("Sending inventory.failed for bookingId={}", event.bookingId());
        kafkaTemplate.send("inventory.failed", String.valueOf(event.bookingId()), event);
    }
}
