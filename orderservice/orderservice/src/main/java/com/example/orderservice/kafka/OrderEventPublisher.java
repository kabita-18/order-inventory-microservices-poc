package com.example.orderservice.kafka;

import com.example.orderservice.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void publishOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {

        kafkaTemplate.send(
                "order.created",
                orderCreatedEvent.getOrderId().toString(),
                orderCreatedEvent
        );
        log.info("Published OrderCreatedEvent for orderId {}",orderCreatedEvent.getOrderId().toString());
    }
}
