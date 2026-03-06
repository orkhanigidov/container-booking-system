package com.example.booking.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryFailedEvent {
    private Long bookingId;
    private String reason;
}
