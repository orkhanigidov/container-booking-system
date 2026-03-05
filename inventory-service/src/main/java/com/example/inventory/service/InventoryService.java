package com.example.inventory.service;

import com.example.inventory.event.BookingCreatedEvent;
import com.example.inventory.event.InventoryFailedEvent;
import com.example.inventory.event.InventoryReservedEvent;
import com.example.inventory.kafka.InventoryEventProducer;
import com.example.inventory.model.Ship;
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
        Ship ship = shipRepository.findById(event.getShipId()).orElse(null);

        if (ship == null) {
            log.warn("Ship not found: {}", event.getShipId());
            producer.sendFailed(new InventoryFailedEvent(event.getBookingId(), "Ship not found"));
            return;
        }

        if (ship.getAvailableSlots() < event.getContainerCount()) {
            log.warn("Not enough slots on ship {}. Available: {}, Requested: {}",
                    ship.getId(), ship.getAvailableSlots(), event.getContainerCount());
            producer.sendFailed(new InventoryFailedEvent(event.getBookingId(), "Not enough slots"));
            return;
        }

        ship.setAvailableSlots(ship.getAvailableSlots() - event.getContainerCount());
        shipRepository.save(ship);
        log.info("Reserved {} slots on ship {} for booking {}",
                event.getContainerCount(), ship.getId(), event.getBookingId());

        producer.sendReserved(new InventoryReservedEvent(
                event.getBookingId(), ship.getId(), event.getContainerCount()
        ));
    }

    @Transactional
    public void release(Long bookingId, String shipId, int containerCount) {
        Ship ship = shipRepository.findById(shipId).orElse(null);
        if (ship == null) {
            log.error("Cannot release slots - ship not found: {}", shipId);
            return;
        }
        ship.setAvailableSlots(ship.getAvailableSlots() + containerCount);
        shipRepository.save(ship);
        log.info("Released {} slots on ship {} for booking {}", containerCount, shipId, bookingId);
    }
}
