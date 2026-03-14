package com.example.inventoryservice.kafka;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.events.OrderCreatedEvent;
import com.example.inventoryservice.events.OrderItemEvent;
import com.example.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class OrderEventConsumer {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order.created", groupId = "inventory-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent orderCreatedEvent) {
        log.info("Received OrderCreatedEvent {}", orderCreatedEvent.getOrderId());

        boolean stockAvailable = true;

        //Validate stock
        for(OrderItemEvent item :  orderCreatedEvent.getItems()) {
            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("product not found"));

            if(product.getStock() < item.getQuantity()) {
                stockAvailable = false;
                break;
            }
        }

        // Deduct or Cancel
        if(stockAvailable) {
            Double totalPrice = 0.0;
            for(OrderItemEvent item :  orderCreatedEvent.getItems()) {
                Product product = productRepository
                        .findById(item.getProductId())
                                .orElseThrow(() -> new RuntimeException("product not found"));

                Long updatedStock = product.getStock() - item.getQuantity();
                product.setStock(updatedStock);
                productRepository.save(product);

                totalPrice += product.getPrice() * item.getQuantity();

            }

            String message = orderCreatedEvent.getOrderId() + ":" + totalPrice;
            kafkaTemplate.send("order.confirmed",
                    orderCreatedEvent.getOrderId().toString(), message);
            log.info("Stock deducted. Order confirmed.");
        }
        else{
            kafkaTemplate.send("order.rejected", orderCreatedEvent.getOrderId().toString(), orderCreatedEvent.getOrderId());
            log.info("Stock deducted. Order rejected.");
        }

    }

}
