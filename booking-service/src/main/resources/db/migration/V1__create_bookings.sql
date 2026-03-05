CREATE TABLE IF NOT EXISTS bookings (
    id              BIGSERIAL    PRIMARY KEY,
    customer_id     VARCHAR(100) NOT NULL,
    ship_id         VARCHAR(100) NOT NULL,
    container_count INT          NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP    NOT NULL
);