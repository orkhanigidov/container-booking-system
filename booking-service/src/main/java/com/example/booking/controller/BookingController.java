package com.example.booking.controller;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking API", description = "Container booking management APIs")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new booking", description = "Submits a new container booking request. The initial status will be PENDING.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BookingResponse create(@RequestBody BookingRequest request) {
        return bookingService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieves the details of a specific booking by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public BookingResponse getById(
            @Parameter(description = "The unique identifier of the booking, e.g. 123")
            @PathVariable Long id) {
        return bookingService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get bookings by customer", description = "Retrieves a list of all container bookings associated with a specific customer ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of bookings"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid customerId parameter")
    })
    public List<BookingResponse> getByCustomer(
            @Parameter(description = "The unique identifier of the customer, e.g. C001")
            @RequestParam String customerId) {
        return bookingService.getByCustomer(customerId);
    }
}
