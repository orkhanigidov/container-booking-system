package com.example.inventory.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String BOOKING_CREATED = "booking.created";
    public static final String INVENTORY_FAILED = "inventory.failed";
    public static final String INVENTORY_RELEASE = "inventory.release";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
}
