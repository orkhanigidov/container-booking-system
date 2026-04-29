package com.example.booking.event;

public record InventoryFailedEvent(
        Long bookingId,
        String reason
) {
}
