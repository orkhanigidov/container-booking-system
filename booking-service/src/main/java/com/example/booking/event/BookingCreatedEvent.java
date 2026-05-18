package com.example.booking.event;

public record BookingCreatedEvent(
        Long bookingId,
        String customerId,
        String customerEmail,
        String shipId,
        int containerCount
) {
}
