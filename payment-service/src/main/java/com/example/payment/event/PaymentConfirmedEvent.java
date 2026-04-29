package com.example.payment.event;

public record PaymentConfirmedEvent(
        Long bookingId
) {
}
