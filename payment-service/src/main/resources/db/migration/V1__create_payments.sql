CREATE TABLE IF NOT EXISTS payments (
    id             BIGSERIAL    PRIMARY KEY,
    booking_id     BIGINT       NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    failure_reason VARCHAR(200),
    processed_at   TIMESTAMP    NOT NULL
);