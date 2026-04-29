package com.example.inventory.kafka;

import com.example.inventory.event.BookingCreatedEvent;
import com.example.inventory.event.InventoryReleaseEvent;
import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "booking.created", groupId = "inventory-service")
    public void onBookingCreated(BookingCreatedEvent event) {
        log.info("Received booking.created for bookingId={}", event.bookingId());
        inventoryService.reserve(event);
    }

    @KafkaListener(topics = "inventory.release", groupId = "inventory-service")
    public void onInventoryRelease(InventoryReleaseEvent event) {
        log.info("Received inventory.release for bookingId={}", event.bookingId());
        inventoryService.release(event.bookingId(), event.shipId(), event.containerCount());
    }
}
