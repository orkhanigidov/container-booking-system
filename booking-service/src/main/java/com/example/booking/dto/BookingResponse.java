package com.example.booking.dto;

import com.example.booking.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String customerId;
    private String shipId;
    private int containerCount;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
