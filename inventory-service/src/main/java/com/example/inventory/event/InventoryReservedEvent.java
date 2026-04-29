package com.example.inventory.event;

public record InventoryReservedEvent(
        Long bookingId,
        String shipId,
        int containerCount
) {
}
