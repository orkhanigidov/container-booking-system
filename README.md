# Container Shipment Booking System

A learning project to explore microservices, Kafka event streaming, and the Saga pattern.
The domain is inspired by container shipping — booking a cargo container on a ship.

## What it does

A customer submits a container booking. The system:
1. Creates the booking (status: PENDING)
2. Checks if the ship has enough available slots
3. Processes a mock payment
4. Confirms or cancels the booking

If the payment fails, the system automatically releases the reserved slots (Saga compensation).

## Architecture

```
POST /bookings
      |
[booking-service :8081]
      | booking.created
      v
[inventory-service :8082] ──> inventory.reserved / inventory.failed
                                       |
                               [payment-service :8083]
                                       | payment.confirmed / payment.failed
                               ┌───────┴────────┐
                     [booking-service]    [notification-service :8084]
                     (update status)       (log email notification)
                           |
                     (if failed) inventory.release
                           |
                     [inventory-service]
                     (release slots)      
```

## Kafka Topics

| Topic              | Producer          | Consumer(s)                           |
|--------------------|-------------------|---------------------------------------|
| booking.created    | booking-service   | inventory-service                     |
| inventory.reserved | inventory-service | payment-service                       |
| inventory.failed   | inventory-service | booking-service                       |
| payment.confirmed  | payment-service   | booking-service, notification-service |
| payment.failed     | payment-service   | booking-service, notification-service |
| inventory.release  | booking-service   | inventory-service                     |

## Tech Stack

- Java 17, Spring Boot 3.5
- Apache Kafka (event streaming)
- PostgreSQL (each service has its own database)
- Flyway (database migrations)
- Testcontainers (integration tests)
- Docker Compose

## Getting Started

### Prerequisites

- Docker & Docker Compose

### Run

```bash
docker compose up --build
```

Wait about 30-40 seconds for all services to start.

### Try it out

**Create a booking:**
```bash
curl -X POST http://localhost:8081/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "shipId": "SH001",
    "containerCount": 10
  }'
```

Response:
```json
{"id": 1, "customerId": "C001", "shipId": "SH001", "containerCount": 10, "status": "PENDING", ...}
```

**Check status (wait 2-3 seconds for events to propagate):**
```bash
curl http://localhost:8081/bookings/1
```

Response will be either:
```json
{"id": 1, ..., "status": "CONFIRMED"}
```
or (20% of the time, due to simulated payment failure):
```json
{"id": 1, ..., "status": "CANCELLED"}
```

**Watch the saga in action:**
```bash
# Open in a separate terminal
docker compose logs -f booking-service inventory-service payment-service notification-service
```

**Check available ships:**
```bash
# After a few confirmed bookings, available slots should decrease
docker compose exec inventory-db psql -U inventory -d inventorydb \
  -c "SELECT id, name, total_slots, available_slots FROM ships;"
```

**Simulate payment failure more often** (set failure rate to 80%):
Edit `payment-service/src/main/resources/application.yml`:
```yaml
app:
  payment:
    failure-rate: 0.8
```

Then rebuild: `docker compose up --build payment-service`

## Running Tests

```bash
cd booking-service
./gradlew test
```

Testcontainers will automatically start PostgreSQL and Kafka in Docker.

## Project Structure

```
container-booking-system/
├── booking-service/       REST API + Saga coordinator
├── inventory-service/     Ship slot management
├── payment-service/       Mock payment processing
├── notification-service/  Email notifications (logged)
├── docker-compose.yml
└── README.md
```

## Known Limitations

- Payment service randomly fails 20% of the time (intentional for testing Saga)
- No retry mechanism if a Kafka consumer crashes mid-processing
- Notification service only logs — no real email sending
- No API authentication