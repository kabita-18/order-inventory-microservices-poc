package com.example.orderservice.controller;


import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private  final OrderService orderService;

    @GetMapping("/helloOrders")
    public String helloOrders() {
        return "Hello from Order Service";
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequestDto){
        OrderRequestDto orderRequestDto1 = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(orderRequestDto1);
    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(HttpServletRequest request) {
        log.info("Fetching all orders from OrderController");
        List<OrderRequestDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getOrderById(@PathVariable Long id) {
        log.info("Fetching orders from OrderController with id{}:", id);
        OrderRequestDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);

    }
    @PostMapping("/cancel-order/{orderId}")
    public ResponseEntity<OrderRequestDto> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }
}
