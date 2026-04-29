package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.event.BookingCreatedEvent;
import com.example.booking.kafka.BookingEventProducer;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingEventProducer producer;

    public BookingResponse create(BookingRequest request) {
        Booking booking = new Booking();
        booking.setCustomerId(request.customerId());
        booking.setShipId(request.shipId());
        booking.setContainerCount(request.containerCount());
        booking.setStatus(BookingStatus.PENDING);
        booking = repository.save(booking);

        producer.sendBookingCreated(new BookingCreatedEvent(
                booking.getId(),
                booking.getCustomerId(),
                booking.getShipId(),
                booking.getContainerCount()
        ));

        return toResponse(booking);
    }

    public BookingResponse getById(Long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        return toResponse(booking);
    }

    public List<BookingResponse> getByCustomer(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateStatus(Long id, BookingStatus status) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        booking.setStatus(status);
        repository.save(booking);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(), b.getCustomerId(), b.getShipId(),
                b.getContainerCount(), b.getStatus(), b.getCreatedAt()
        );
    }
}
