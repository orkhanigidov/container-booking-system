package com.example.booking.dto;

public record BookingRequest(
        String customerId,
        String shipId,
        int containerCount
) {
}
