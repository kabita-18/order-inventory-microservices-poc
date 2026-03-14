package com.example.inventoryservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private List<OrderItemEvent> items;
}
