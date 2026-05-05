package com.example.inventory.service;

import com.example.inventory.event.BookingCreatedEvent;
import com.example.inventory.event.InventoryFailedEvent;
import com.example.inventory.event.InventoryReservedEvent;
import com.example.inventory.kafka.InventoryEventProducer;
import com.example.inventory.repository.ShipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ShipRepository shipRepository;
    private final InventoryEventProducer producer;

    @Transactional
    public void reserve(BookingCreatedEvent event) {
        shipRepository.findById(event.shipId()).ifPresentOrElse(
                ship -> {
                    if (ship.getAvailableSlots() < event.containerCount()) {
                        log.warn("Not enough slots on ship {}. Available: {}, Requested: {}",
                                ship.getId(), ship.getAvailableSlots(), event.containerCount());
                        producer.sendFailed(new InventoryFailedEvent(event.bookingId(), "Not enough slots"));
                        return;
                    }

                    ship.setAvailableSlots(ship.getAvailableSlots() - event.containerCount());
                    shipRepository.save(ship);
                    log.info("Reserved {} slots on ship {} for booking {}", event.containerCount(), ship.getId(), event.bookingId());
                    producer.sendReserved(new InventoryReservedEvent(event.bookingId(), ship.getId(), event.containerCount()));
                }, () -> {
                    log.warn("Ship not found: {}", event.shipId());
                    producer.sendFailed(new InventoryFailedEvent(event.bookingId(), "Ship not found"));
                }
        );
    }

    @Transactional
    public void release(Long bookingId, String shipId, int containerCount) {
        shipRepository.findById(shipId).ifPresentOrElse(
                ship -> {
                    ship.setAvailableSlots(ship.getAvailableSlots() + containerCount);
                    shipRepository.save(ship);
                    log.info("Released {} slots on ship {} for booking {}", containerCount, shipId, bookingId);
                }, () -> {
                    log.error("Cannot release slots - ship not found: {}", shipId);
                }
        );
    }
}
