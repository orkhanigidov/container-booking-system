package com.example.payment.kafka;

import com.example.payment.event.InventoryReservedEvent;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "inventory.reserved", groupId = "payment-service")
    public void onInventoryReserved(InventoryReservedEvent event) {
        log.info("Received inventory.reserved for bookingId={}", event.getBookingId());
        paymentService.process(event);
    }
}
