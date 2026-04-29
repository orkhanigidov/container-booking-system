package com.example.booking.event;

public record PaymentConfirmedEvent(
        Long bookingId
) {
}
