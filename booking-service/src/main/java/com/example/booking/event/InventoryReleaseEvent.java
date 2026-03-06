package com.example.booking.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReleaseEvent {
    private Long bookingId;
    private String shipId;
    private int containerCount;
}
