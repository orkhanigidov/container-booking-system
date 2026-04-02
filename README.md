# Container Shipment Booking System

[![CI/CD Pipeline](https://github.com/orkhanigidov/container-booking-system/actions/workflows/ci.yml/badge.svg)](https://github.com/orkhanigidov/container-booking-system/actions/workflows/ci.yml)

A learning project to explore microservices, Kafka event streaming, and the Saga pattern, and modern deployment
strategies (Docker Compose, Kubernetes, and Helm).
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
- Docker Compose, Kubernetes, Helm

## Getting Started

You can run this project using either Docker Compose (easiest for quick local testing) or Kubernetes / Helm (best for
production environments).

### Option 1: Running with Docker Compose

**1. Start the environment:**

```bash
docker compose up --build
```

Wait about 30-40 seconds for all services to start.

**2. Stop the environment:**

```bash
docker compose down -v
```

### Option 2: Running with Kubernetes & Helm

**Prerequisites:** A local Kubernetes cluster (like Docker Desktop K8s or Minikube) and Helm installed.

**1. Build Docker images locally:**

```bash
docker build -t booking-service:latest -f booking-service/Dockerfile .
docker build -t inventory-service:latest -f inventory-service/Dockerfile .
docker build -t payment-service:latest -f payment-service/Dockerfile .
docker build -t notification-service:latest -f notification-service/Dockerfile .
```

**2. Deploy using Helm:**

```bash
helm install my-booking-system ./helm/booking-chart
```

**3. Verify the deployment:**

```bash
kubectl get pods -w
```

Wait until all pods show 1/1 Running.

**4. Port-forward the Booking Service API:**

```bash
kubectl port-forward svc/booking-service 8081:8081
```

**5. Uninstall / Cleanup:**

```bash
helm uninstall my-booking-system
```

Note: Raw Kubernetes manifests are also available in the k8s/ directory for learning purposes.

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
{
  "id": 1,
  "customerId": "C001",
  "shipId": "SH001",
  "containerCount": 10,
  "status": "PENDING",
  ...
}
```

**Check status (wait 2-3 seconds for events to propagate):**

```bash
curl http://localhost:8081/bookings/1
```

Response will be either:

```json
{
  "id": 1,
  ...,
  "status": "CONFIRMED"
}
```

or (20% of the time, due to simulated payment failure):

```json
{
  "id": 1,
  ...,
  "status": "CANCELLED"
}
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

## API Documentation (Swagger UI)

Interactive API docs are available at:

```
http://localhost:8081/swagger-ui.html
```

## Running Tests

To run the unit and integration tests across the services:

```bash
# Run tests for booking-service (includes Testcontainers for DB and Kafka)
cd booking-service
./gradlew test

# Run tests for notification-service (includes Mockito tests for email sending)
cd notification-service
./gradlew test
```

Testcontainers will automatically start PostgreSQL and Kafka in Docker.

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration. The pipeline automatically triggers on every push and pull
request to the `master` branch.

It performs the following steps:

1. Sets up the Java 17 environment.
2. Caches Gradle dependencies to speed up builds.
3. Runs all unit and integration tests across all microservices (spinning up PostgreSQL and Kafka via Testcontainers).
4. Builds the Docker images for all four services to ensure the `Dockerfile` configurations remain valid.

## Project Structure

```
container-booking-system/
├── booking-service/       REST API + Saga coordinator
├── inventory-service/     Ship slot management
├── payment-service/       Mock payment processing
├── notification-service/  Email notifications (logged)
├── k8s/                   Raw Kubernetes manifests (learning/testing)
├── helm/booking-chart/    Helm chart for automated K8s deployment
├── docker-compose.yml     Docker Compose configuration
└── README.md
```

## Known Limitations

- Payment service randomly fails 20% of the time (intentional for testing Saga)
- No API authentication