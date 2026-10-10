# Deployment plan

## Docker
Build locally:
```bash
docker build -t orderflow:local .
```

## Kubernetes / Rancher
- Use `kubectl` or Rancher to manage the target Kubernetes cluster.
- Create namespace: `kubectl apply -f k8s/namespace.yaml`
- Before deploying, create the `orderflow-secrets` Secret with `mongodb-uri`.
- Replace the sample image in `k8s/deployment.yaml` with the published image.
- MongoDB, Redis, Kafka and Schema Registry should use managed services or separately managed StatefulSets/Helm releases; the sample deployment intentionally does not install them.
- Add NetworkPolicies, PodDisruptionBudgets, autoscaling, resource quotas, TLS/Ingress, secrets management and backup/restore before production.

## AWS target
Recommended learning deployment: EKS for Kubernetes, ECR for container images, IAM OIDC for GitHub Actions, Secrets Manager for secrets, CloudWatch or selected observability vendor, and managed database/messaging choices where appropriate.

Do not store long-lived AWS access keys in GitHub secrets if GitHub OIDC federation can be used. Configure an AWS IAM role trusted by the GitHub Actions OIDC provider and scope permissions to the target ECR/EKS resources.

## GitHub Actions release workflow
The included workflow is CI only. Add a separate deploy workflow after the image build, registry, AWS IAM role, cluster and environment approvals are configured. Do not enable an automatic production deployment until secrets and access policies are reviewed.
