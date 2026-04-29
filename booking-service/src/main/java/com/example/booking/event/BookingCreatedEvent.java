package com.example.booking.event;

public record BookingCreatedEvent(
        Long bookingId,
        String customerId,
        String shipId,
        int containerCount
) {
}
