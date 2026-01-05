# Order–Inventory Microservices POC

This POC demonstrates an end-to-end order lifecycle using microservices.

## Features
- Create Order with Inventory stock reduction
- Cancel Order with Inventory stock restocking
- Inter-service communication using OpenFeign
- Resilience4j Circuit Breaker for fault tolerance
- Graceful fallback handling during Inventory downtime

## Flow
1. Order Service creates an order and reduces inventory stock
2. Order Service cancels an order and restocks inventory
3. Circuit Breaker prevents cascading failures when Inventory is unavailable

## Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- Spring Cloud OpenFeign
- Resilience4j Circuit Breaker
- MySQL / PostgreSQL
