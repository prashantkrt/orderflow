# OrderFlow

**OrderFlow** is an event-driven order management platform built with Java 25, Spring Boot 4, MongoDB, Redis, Kafka, and Avro. It is designed as a portfolio-grade engineering project that grows in clear phases.

> Status: repository scaffold and local infrastructure configuration. Treat integrations as work in progress until built, tested, and deployed. Do not claim production readiness or working integrations before verification.

## Technology plan

- Java 25 and Spring Boot 4
- MongoDB for order documents
- Redis for cache-aside reads
- Apache Kafka for domain events
- Apache Avro + Confluent Schema Registry for schema-based serialization
- Spring Boot Actuator, Micrometer and OpenTelemetry
- Splunk and Datadog integration paths
- Docker, Kubernetes, Rancher
- GitHub Actions CI/CD and AWS deployment

## Architecture target

```text
Client / API
    |
    v
Order API (Spring Boot)
    |---- MongoDB (source of truth)
    |---- Redis (short-lived read cache)
    |
    +---- OrderCreated Avro event ----> Kafka
                                           |
                                           v
                                 Inventory / Notification consumers
                                           |
                                 Schema Registry (Avro schemas)

Telemetry: Actuator + Micrometer + OpenTelemetry
    |---- Datadog
    +---- Splunk Observability / Splunk platform (depending on collector/export setup)

Delivery: GitHub Actions -> tests -> container image -> registry -> AWS EKS
Kubernetes operations: kubectl / Helm / Rancher
```

## Local prerequisites

- JDK 25
- Maven 3.9+
- Docker Desktop
- Git and a GitHub account

## Start local infrastructure

```bash
docker compose up -d
docker compose ps
```

Services:
- MongoDB: `localhost:27017`
- Redis: `localhost:6379`
- Kafka: `localhost:9092`
- Schema Registry: `http://localhost:8081`

The Compose file is for local development only. It uses a single Kafka broker with plaintext listeners and sample local credentials. Do not expose it to the public internet.

## Build and run

```bash
mvn clean verify
mvn spring-boot:run
```

Health endpoint: `http://localhost:8080/actuator/health`.

## Project conventions

- MongoDB is the source of truth; Redis is only a cache.
- Events should be published only after the order is durably saved. For stronger delivery guarantees, implement the transactional outbox pattern rather than assuming a database write and Kafka publish are one atomic transaction.
- Use Avro schema compatibility rules and a Schema Registry. Do not hand-roll binary serialization or silently change event schemas.
- Never commit credentials, API keys, AWS keys, kubeconfigs, Datadog API keys, or Splunk tokens.
- Pin production image versions and scan images before deployment.

## Roadmap

- [ ] Implement and test Order REST APIs and MongoDB persistence
- [ ] Add Redis cache-aside behavior and invalidation tests
- [ ] Add Avro schema and generated SpecificRecord classes
- [ ] Add Kafka producer/consumer with Schema Registry
- [ ] Add retry, dead-letter topic, idempotent consumer and outbox pattern
- [ ] Add API security and OpenAPI documentation
- [ ] Add Actuator, Micrometer, tracing and structured JSON logs
- [ ] Add Datadog and Splunk deployment-specific configuration
- [ ] Add Docker image, SBOM and vulnerability scanning
- [x] Add starter Kubernetes manifests and Helm chart under `helm/orderflow/` (validate against a real cluster before production)
- [ ] Add GitHub Actions CI and AWS EKS deployment workflow

## License

MIT. See `LICENSE`.
