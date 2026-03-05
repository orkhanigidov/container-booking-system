package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    private String customerId;
    private String shipId;
    private int containerCount;
}
