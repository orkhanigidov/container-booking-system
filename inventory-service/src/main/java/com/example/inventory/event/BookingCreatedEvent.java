package com.example.inventory.event;

public record BookingCreatedEvent(
        Long bookingId,
        String customerId,
        String shipId,
        int containerCount
) {
}
