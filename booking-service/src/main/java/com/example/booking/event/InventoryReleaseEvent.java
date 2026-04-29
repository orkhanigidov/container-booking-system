package com.example.booking.event;

public record InventoryReleaseEvent(
        Long bookingId,
        String shipId,
        int containerCount
) {
}
