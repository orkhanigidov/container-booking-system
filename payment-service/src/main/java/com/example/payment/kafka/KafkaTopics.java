package com.example.payment.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String PAYMENT_CONFIRMED = "payment.confirmed";
    public static final String PAYMENT_FAILED = "payment.failed";
}
