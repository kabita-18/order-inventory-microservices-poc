package com.example.orderservice.service;


import com.example.orderservice.dto.OrderRequestDto;

import java.util.List;

public interface OrderService {
    List<OrderRequestDto> getAllOrders();

    OrderRequestDto createOrder(OrderRequestDto orderRequestDto);

    OrderRequestDto getOrderById(Long id);

    OrderRequestDto cancelOrder(Long orderId);
}
