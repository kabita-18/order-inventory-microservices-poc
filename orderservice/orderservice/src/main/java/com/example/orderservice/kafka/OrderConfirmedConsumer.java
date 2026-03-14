package com.example.orderservice.kafka;

import com.example.orderservice.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmedConsumer {

    private final OrderServiceImpl orderService;

    @KafkaListener(topics = "order.confirmed", groupId = "order-group")
    public void consume(String message) {

        log.info("Order confirmed event received {}", message);

        // Remove surrounding quotes if present
        message = message.replaceAll("\"", "");

        String[] parts = message.split(":");
        Long orderId = Long.parseLong(parts[0]);
        Double totalPrice = parts.length > 1 ? Double.parseDouble(parts[1]) : null;

        orderService.handleOrderConfirmed(orderId, totalPrice);
    }
}