package com.example.booking.dto;

import com.example.booking.model.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String customerId,
        String shipId,
        int containerCount,
        BookingStatus status,
        LocalDateTime createdAt
) {
}
