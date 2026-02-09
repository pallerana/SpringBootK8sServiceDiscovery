# Spring Boot Service Discovery on Kubernetes

Production-grade illustration of **service discovery** for Spring Boot apps on Kubernetes: a **book-service** (provider) and **client-service** (consumer) that discovers and calls it via Kubernetes DNS and Spring Cloud Kubernetes.

## Concepts

- **Kubernetes Service** exposes a stable DNS name (`book-service.<namespace>.svc.cluster.local`) and load-balances to pod IPs.
- **Spring Cloud Kubernetes** provides a `DiscoveryClient` and integrates with **Spring Cloud Load Balancer** so the client resolves the logical name `book-service` to live endpoints.
- **client-service** uses a load-balanced `WebClient` to call `http://book-service/books`; the host is resolved by the cluster.

## Project layout

```
├── book-service/          # REST API: GET /books, GET /books/{id}
├── client-service/        # Discovers book-service via K8s, exposes GET /catalog/books
├── k8s/                   # Namespace, Deployments, Services, RBAC
└── build.gradle           # Multi-module root
```

## Build

```bash
./gradlew clean build
```

## Run locally (no Kubernetes)

1. Start book-service: `./gradlew :book-service:bootRun`
2. Start client-service with local profile: `./gradlew :client-service:bootRun --args='--spring.profiles.active=local'`
3. Call client: `curl http://localhost:8081/catalog/books`

Local mode uses a fixed URL (`app.book-service.url`, default `http://localhost:8080`) and disables Kubernetes discovery.

## Run on Kubernetes

### 1. Build JARs and images

```bash
./gradlew :book-service:bootJar :client-service:bootJar

docker build -t book-service:0.0.1-SNAPSHOT ./book-service
docker build -t client-service:0.0.1-SNAPSHOT ./client-service
```

For minikube: `eval $(minikube docker-env)` then the same `docker build` commands.

### 2. Deploy

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/client-service/rbac.yaml
kubectl apply -f k8s/book-service/
kubectl apply -f k8s/client-service/
```

### 3. Use

- **From inside the cluster** (e.g. another pod in `service-discovery-demo`):
  - Book-service: `http://book-service:8080/books`
  - Client (which uses discovery to call book-service): `http://client-service:8081/catalog/books`
- **From your machine** (port-forward):
  - `kubectl port-forward -n service-discovery-demo svc/client-service 8081:8081`
  - `curl http://localhost:8081/catalog/books`

## Configuration

| Component        | Purpose |
|-----------------|--------|
| **book-service** | `spring.application.name=book-service` (must match K8s Service name). |
| **client-service** | `spring.cloud.kubernetes.discovery` (namespace, port name). Uses `LoadBalancedExchangeFilterFunction` so `http://book-service/...` is resolved via `DiscoveryClient`. |
| **RBAC**        | client-service uses a ServiceAccount with Role listing Services/Endpoints/Pods (and optionally EndpointSlices) so the Kubernetes API can be used for discovery. |

## Health and resilience

- Both apps use Spring Boot **liveness/readiness**; K8s Deployments use `/actuator/health/liveness` and `/actuator/health/readiness`.
- client-service uses **timeouts** on the WebClient and maps failures to 502 with a clear error body.
- No boilerplate: interfaces for the book client, single implementation, minimal DTOs.

## References

- [Service Discovery for Spring Boot App using Kubernetes](https://medium.com/@bubu.tripathy/service-discovery-for-spring-boot-app-using-kubernetes-2bdb011397fd)
- [Spring Cloud Kubernetes](https://docs.spring.io/spring-cloud-kubernetes/reference/html/)
- [Kubernetes Services](https://kubernetes.io/docs/concepts/services-networking/service/)
