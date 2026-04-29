package com.example.booking.event;

public record PaymentFailedEvent(
        Long bookingId,
        String shipId,
        int containerCount,
        String reason
) {
}
