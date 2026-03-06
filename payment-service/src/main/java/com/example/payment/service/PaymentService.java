package com.example.payment.service;

import com.example.payment.event.InventoryReservedEvent;
import com.example.payment.event.PaymentConfirmedEvent;
import com.example.payment.event.PaymentFailedEvent;
import com.example.payment.kafka.PaymentEventProducer;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer producer;
    private final Random random = new Random();

    @Value("${app.payment.failure-rate:0.2}")
    private double failureRate;

    public void process(InventoryReservedEvent event) {
        boolean shouldFail = random.nextDouble() < failureRate;

        Payment payment = new Payment();
        payment.setBookingId(event.getBookingId());

        if (shouldFail) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Insufficient funds");
            paymentRepository.save(payment);
            log.warn("Payment FAILED for bookingId={}", event.getBookingId());
            producer.sendFailed(new PaymentFailedEvent(
                    event.getBookingId(),
                    event.getShipId(),
                    event.getContainerCount(),
                    "Insufficient funds"
            ));
        } else {
            payment.setStatus(PaymentStatus.CONFIRMED);
            paymentRepository.save(payment);
            log.info("Payment CONFIRMED for bookingId={}", event.getBookingId());
            producer.sendConfirmed(new PaymentConfirmedEvent(event.getBookingId()));
        }
    }
}
