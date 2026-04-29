package com.example.payment.event;

public record PaymentFailedEvent(
        Long bookingId,
        String shipId,
        int containerCount,
        String reason
) {
}
