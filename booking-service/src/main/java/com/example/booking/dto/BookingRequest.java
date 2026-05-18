package com.example.booking.dto;

public record BookingRequest(
        String customerId,
        String customerEmail,
        String shipId,
        int containerCount
) {
}
