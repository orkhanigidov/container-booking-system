package com.example.inventory.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryReleaseEvent {
    private Long bookingId;
    private String shipId;
    private int containerCount;
}
