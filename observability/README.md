# Observability: metrics, logs, traces

## Application instrumentation
- Actuator health, metrics and Prometheus endpoint are configured.
- OpenTelemetry dependency is included; exporter/collector configuration still needs to be added for your selected backend.
- Keep JSON logs on stdout in containers and include trace/correlation IDs.

## Datadog
Choose one deployment approach:
1. Datadog Agent + Kubernetes integration and Autodiscovery annotations, or
2. OpenTelemetry Collector exporting traces/metrics/logs to Datadog.

Store `DD_API_KEY` in a Kubernetes Secret or AWS Secrets Manager/External Secrets. Never commit it. Configure service name `orderflow`, environment `dev`/`staging`/`prod`, and version from the image tag.

## Splunk
Choose Splunk Observability Cloud or Splunk Enterprise/Cloud Platform based on the goal:
- Observability Cloud: OpenTelemetry Collector with Splunk distribution/exporters for metrics, traces and logs.
- Splunk Platform: forward structured application logs through a supported collector/forwarder; define indexes, sourcetypes and retention with your administrator.

Do not send the same telemetry to both systems by default without a cost/retention plan. Decide which system is the source of truth for alerts and dashboards.

## Suggested starter dashboards
- HTTP request rate, latency percentiles and 4xx/5xx
- JVM heap, GC pauses, threads
- MongoDB operation latency and connection pool
- Redis hit/miss ratio and latency
- Kafka consumer lag, producer errors, retries and dead-letter count
- Order creation success/failure rate
