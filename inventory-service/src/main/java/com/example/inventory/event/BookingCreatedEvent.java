package com.example.inventory.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookingCreatedEvent {
    private Long bookingId;
    private String customerId;
    private String shipId;
    private int containerCount;
}
