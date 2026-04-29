package com.example.inventory.event;

public record InventoryFailedEvent(
        Long bookingId,
        String reason
) {
}
