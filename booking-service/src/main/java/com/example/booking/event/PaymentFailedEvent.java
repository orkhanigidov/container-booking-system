package com.example.booking.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentFailedEvent {
    private Long bookingId;
    private String shipId;
    private int containerCount;
    private String reason;
}
