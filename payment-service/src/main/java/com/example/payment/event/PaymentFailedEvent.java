package com.example.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {
    private Long bookingId;
    private String shipId;
    private int containerCount;
    private String reason;
}
