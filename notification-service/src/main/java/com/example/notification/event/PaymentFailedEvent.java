package com.example.notification.event;

public record PaymentFailedEvent(
        Long bookingId,
        String shipId,
        int containerCount,
        String reason
) {
}
