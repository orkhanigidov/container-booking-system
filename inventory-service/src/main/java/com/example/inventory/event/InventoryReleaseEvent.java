package com.example.inventory.event;

public record InventoryReleaseEvent(
        Long bookingId,
        String shipId,
        int containerCount
) {
}
