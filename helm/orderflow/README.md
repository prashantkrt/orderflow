# OrderFlow Helm chart

This chart deploys the OrderFlow API and can optionally deploy a separate retry workload. MongoDB, Redis, Kafka and Schema Registry are expected to be provided separately (managed services or separately managed Helm releases).

## Prerequisites
- Kubernetes cluster reachable by kubectl (Rancher-managed clusters are supported)
- Helm 3
- A published OrderFlow container image
- Secret `orderflow-secrets` in the release namespace with key `mongodb-uri`

Create the database connection secret without committing it:
```bash
kubectl create namespace orderflow
kubectl create secret generic orderflow-secrets \
  --namespace orderflow \
  --from-literal=mongodb-uri='YOUR_MONGODB_CONNECTION_URI'
```

Update `values.yaml` image.repository and image.tag for your image, then validate:
```bash
helm lint ./helm/orderflow
helm template orderflow ./helm/orderflow --namespace orderflow
```

Install or upgrade:
```bash
helm upgrade --install orderflow ./helm/orderflow \
  --namespace orderflow --create-namespace \
  --set image.repository=ghcr.io/YOUR_GITHUB_USERNAME/orderflow \
  --set image.tag=latest
```

## Environment values

Use one standard values file for the target environment:
```bash
helm upgrade --install orderflow ./helm/orderflow \
  --namespace orderflow --create-namespace \
  -f ./helm/orderflow/environments/qa.yaml
```

Available standard files are `qa.yaml`, `stg.yaml`, `prod.yaml`, and `perf.yaml`.
Each has a corresponding `*-retry.yaml` file for retry deployment sizing. The
retry Deployment is disabled by default because this application does not yet
provide a retry-worker entrypoint. To enable it after providing a worker command,
layer both files and set `retry.enabled=true` plus the worker command, for example:
```bash
helm upgrade --install orderflow ./helm/orderflow \
  --namespace orderflow \
  -f ./helm/orderflow/environments/qa.yaml \
  -f ./helm/orderflow/environments/qa-retry.yaml \
  --set retry.enabled=true \
  --set 'retry.command[0]=/path/to/retry-worker'
```

Enable HPA only when the cluster has Metrics Server and resource requests configured:
```bash
helm upgrade --install orderflow ./helm/orderflow \
  --namespace orderflow --set autoscaling.enabled=true
```

## Security notes
- Do not commit secrets or real credentials in `values.yaml`.
- Configure TLS/Ingress, NetworkPolicies, PodDisruptionBudget, resource quotas and secret management before production.
- Replace the sample image with a versioned image tag or digest; avoid `latest` for production.
- Confirm the container image actually supports non-root execution before enabling `runAsNonRoot`.
