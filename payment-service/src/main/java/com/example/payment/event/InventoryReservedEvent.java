package com.example.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryReservedEvent {
    private Long bookingId;
    private String shipId;
    private int containerCount;
}
