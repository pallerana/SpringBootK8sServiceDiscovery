# Spring Boot Service Discovery on Kubernetes

Production-grade illustration of **service discovery** for Spring Boot apps on Kubernetes: a **book-service** (provider) and **client-service** (consumer) that discovers and calls it via Kubernetes DNS and Spring Cloud Kubernetes.

**Requirements:** Java 17+ (Gradle 9.x and this project require Java 17 to run.)

---

## Concepts

- **Kubernetes Service** exposes a stable DNS name (`book-service.<namespace>.svc.cluster.local`) and load-balances to pod IPs.
- **Spring Cloud Kubernetes** provides a `DiscoveryClient` and integrates with **Spring Cloud Load Balancer** so the client resolves the logical name `book-service` to live endpoints.
- **client-service** calls `http://book-service/books` using a configurable HTTP client (WebClient, RestTemplate, or RestClient); the host is resolved by the cluster.

---

## Project layout

```
├── book-service/              # REST API: GET /books, GET /books/{id}
│   ├── src/
│   └── Dockerfile
├── client-service/            # Discovers book-service via K8s; exposes GET /catalog/books
│   ├── src/
│   └── Dockerfile
├── k8s/                       # Kubernetes manifests
│   ├── namespace.yaml
│   ├── kustomization.yaml
│   ├── book-service/          # Deployment, Service
│   └── client-service/        # Deployment, Service, RBAC
├── build.gradle
├── settings.gradle
└── README.md
```

---

## Build

```bash
./gradlew clean build
```

Windows (PowerShell):

```powershell
.\gradlew clean build
```

---

## Run locally (no Kubernetes)

1. **Start book-service** (port 8080):
   ```bash
   ./gradlew :book-service:bootRun
   ```

2. **Start client-service** (port 8081) with the local profile:
   ```bash
   ./gradlew :client-service:bootRun --args='--spring.profiles.active=local'
   ```

3. **Call the client:**
   ```bash
   curl http://localhost:8081/catalog/books
   curl http://localhost:8081/catalog/books/1
   ```

Local mode uses a fixed URL (`app.book-service.url`, default `http://localhost:8080`) and disables Kubernetes discovery.

---

## Run on Kubernetes

### 1. Build JARs and images

```bash
./gradlew :book-service:bootJar :client-service:bootJar

docker build -t book-service:0.0.1-SNAPSHOT ./book-service
docker build -t client-service:0.0.1-SNAPSHOT ./client-service
```

For **minikube**, point Docker at the cluster then build:

```bash
eval $(minikube docker-env)
docker build -t book-service:0.0.1-SNAPSHOT ./book-service
docker build -t client-service:0.0.1-SNAPSHOT ./client-service
```

### 2. Deploy

**Option A – Kustomize (recommended):**

```bash
kubectl apply -k k8s/
```

**Option B – Plain manifests:**

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/client-service/rbac.yaml
kubectl apply -f k8s/book-service/
kubectl apply -f k8s/client-service/
```

### 3. Use

- **From inside the cluster** (e.g. another pod in `service-discovery-demo`):
  - Book-service: `http://book-service:8080/books`
  - Client: `http://client-service:8081/catalog/books`
- **From your machine** (port-forward):
  ```bash
  kubectl port-forward -n service-discovery-demo svc/client-service 8081:8081
  curl http://localhost:8081/catalog/books
  ```

---

## Configuration

### client-service: HTTP client choice

Choose how client-service calls book-service via **`app.book-service.client-type`**:

| Value           | Implementation | Notes                          |
|----------------|----------------|---------------------------------|
| `reactive`     | WebClient      | Default; non-blocking, reactive |
| `rest-template`| RestTemplate   | Blocking; classic Spring client |
| `rest-client`  | RestClient     | Blocking; Spring 6.1+ sync API  |

Example (YAML):

```yaml
app:
  book-service:
    client-type: rest-client   # or reactive | rest-template
```

Or at runtime: `--app.book-service.client-type=rest-client`

All three use the same load-balanced discovery in Kubernetes (and the same fixed URL in local profile).

### Other settings

| Component       | Purpose |
|-----------------|--------|
| **book-service** | `spring.application.name=book-service` (must match the K8s Service name). |
| **client-service** | `spring.cloud.kubernetes.discovery` (namespace, port name). In local profile, `app.book-service.url` and `app.book-service.discovery-enabled: false`. |
| **RBAC**        | client-service uses a ServiceAccount with Role (Services, Endpoints, Pods) so the Kubernetes API can be used for discovery. |

---

## Health and resilience

- Both apps expose Spring Boot **liveness and readiness**; K8s Deployments probe `/actuator/health/liveness` and `/actuator/health/readiness`.
- client-service uses **timeouts** on outbound calls and maps upstream failures to **502** with a structured error body.
- **Factory pattern** for the book client: one interface, three implementations (reactive, RestTemplate, RestClient), selected by configuration.

---

## References

- [Service Discovery for Spring Boot App using Kubernetes](https://medium.com/@bubu.tripathy/service-discovery-for-spring-boot-app-using-kubernetes-2bdb011397fd)
- [Spring Cloud Kubernetes](https://docs.spring.io/spring-cloud-kubernetes/reference/html/)
- [Kubernetes Services](https://kubernetes.io/docs/concepts/services-networking/service/)
