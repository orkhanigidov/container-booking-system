package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BookingRequest(
        @NotBlank(message = "customerId must not be blank")
        String customerId,

        @NotBlank(message = "customerEmail must not be blank")
        String customerEmail,

        @NotBlank(message = "shipId must not be blank")
        String shipId,

        @Positive(message = "containerCount must be a positive number")
        int containerCount
) {
}
