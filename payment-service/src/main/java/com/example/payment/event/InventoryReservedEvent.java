package com.example.payment.event;

public record InventoryReservedEvent(
        Long bookingId,
        String shipId,
        int containerCount
) {
}
