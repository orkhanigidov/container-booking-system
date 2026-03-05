package com.example.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFailedEvent {
    private Long bookingId;
    private String reason;
}
