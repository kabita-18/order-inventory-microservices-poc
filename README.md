# ECommerce Microservices POC

An end-to-end order lifecycle management system built with Spring Boot microservices,
event-driven architecture using Apache Kafka, and resilience patterns.

## Architecture
```
[API Gateway :8084] → [Order Service :8082] → Kafka → [Inventory Service :8081]
                              ↑                               ↓
                        [Eureka Server :8083]          [MySQL Database]
```

## Features
- Create Order with async inventory stock reduction via Kafka
- Cancel Order with inventory restocking via OpenFeign
- Event-driven communication using Apache Kafka (KRaft mode)
- Eureka Service Discovery for dynamic service registration
- API Gateway for unified entry point and routing
- Resilience4j Circuit Breaker for fault tolerance
- Graceful fallback handling during service downtime
- Order status lifecycle: PENDING → CONFIRMED / REJECTED / CANCELLED

## Order Flow
1. Client sends order request to API Gateway (port 8084)
2. Order Service saves order with PENDING status and publishes `order.created` event
3. Inventory Service consumes event, validates stock, deducts quantity, calculates total price
4. Inventory Service publishes `order.confirmed` or `order.rejected` event
5. Order Service consumes confirmation, updates order status and total price

## Kafka Topics
| Topic | Producer | Consumer |
|---|---|---|
| `order.created` | Order Service | Inventory Service |
| `order.confirmed` | Inventory Service | Order Service |
| `order.rejected` | Inventory Service | Order Service |

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Cloud (Eureka, Gateway, OpenFeign)
- Apache Kafka (KRaft mode - no Zookeeper)
- Resilience4j Circuit Breaker
- Spring Data JPA
- MySQL
- Docker (Kafka UI)
- Lombok
- ModelMapper

## Services
| Service | Port | Description |
|---|---|---|
| Eureka Server | 8083 | Service discovery and registration |
| API Gateway | 8084 | Unified entry point, load balancing |
| Order Service | 8082 | Order management and lifecycle |
| Inventory Service | 8081 | Stock management and validation |

## Running the Project

### Prerequisites
- Java 17+
- MySQL running on port 3306
- Apache Kafka running on port 9092 (KRaft mode)
- Docker (for Kafka UI)

### Start Kafka
```cmd
D:\downloads\kafka\bin\windows\kafka-server-start.bat D:\downloads\kafka\config\server.properties
```

### Start Kafka UI (Docker)
```cmd
docker start kafka-ui
```

### Create Kafka Topics
```cmd
kafka-topics.bat --create --topic order.created --bootstrap-server localhost:9092
kafka-topics.bat --create --topic order.confirmed --bootstrap-server localhost:9092
kafka-topics.bat --create --topic order.rejected --bootstrap-server localhost:9092
```

### Start Services (in order)
1. Eureka Server
2. API Gateway
3. Order Service
4. Inventory Service

## API Endpoints

### Order Service (via Gateway)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/order/core/create-order` | Create a new order |
| GET | `/api/v1/order/core/get-all-orders` | Get all orders |
| GET | `/api/v1/order/core/{id}` | Get order by ID |
| DELETE | `/api/v1/order/core/cancel/{id}` | Cancel an order |

### Inventory Service (via Gateway)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/inventory/api/v1/products` | Get all products |
| GET | `/inventory/api/v1/products/{id}` | Get product by ID |

## Database Setup
```sql
CREATE DATABASE order_db;
CREATE DATABASE inventory_db;
```

## Monitoring
- Eureka Dashboard: http://localhost:8083
- Kafka UI: http://localhost:8080
- Actuator Health: http://localhost:8082/actuator/health