package com.example.orderservice.service;

import com.example.orderservice.clients.InventoryOpenFeignClient;
import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;

    @Override
    public List<OrderRequestDto> getAllOrders() {
        log.info("Fetching all Orders from Order Service");
        List<Orders> orders =  orderRepository.findAll();
        return orders.stream().map( order -> modelMapper.map(order, OrderRequestDto.class)).toList();
    }

//    @Retry(name="inventoryRetry", fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name ="inventoryCircuitBreaker", fallbackMethod = "createOrderFallback")
//    @RateLimiter(name = "inventoryRateLimiter", fallbackMethod = "createOrderFallback")
    @Override
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Creating Order Request from Order Service");
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);
        Orders orders = modelMapper.map(orderRequestDto, Orders.class);
        for(OrderItem orderItem: orders.getOrderItems()) {
            orderItem.setOrder(orders);
        }
//        orders.setTotalPrice(orders.getTotalPrice());
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders saveOrder = orderRepository.save(orders);
        return modelMapper.map(saveOrder, OrderRequestDto.class);

    }


    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Creating Order Fallback from Order Service: {}", throwable.getMessage());

        return new OrderRequestDto();

    }

    @Override
    public OrderRequestDto getOrderById(Long id) {
        log.info("Fetching Order Request from Order Service by id {}" , id);
        Orders order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order with id " + id + " not found"));
        return modelMapper.map(order, OrderRequestDto.class);
    }

    @CircuitBreaker(name="inventoryCircuitBreaker", fallbackMethod = "restockFallback")
    @Override
    @Transactional
    public OrderRequestDto cancelOrder(Long orderId) {
        log.info("Canceling Order Request from Order Service by id {}" , orderId);

//        Fetch order entity
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order with id " + orderId + " not found"));

        //Prevent double cancellation
        if(order.getOrderStatus() == OrderStatus.CANCELLED){
            throw  new RuntimeException("Order with id " + orderId+ " already cancelled");
        }

        //Build DTO for inventory service
        OrderRequestDto orderRequestDto = modelMapper.map(order, OrderRequestDto.class);
        //Call Inventory Service to restock
        inventoryOpenFeignClient.restock(orderRequestDto);
        // Fetch existing order
        Orders orders = orderRepository.findById(orderRequestDto.getId()).orElseThrow(() -> new RuntimeException("Order with id " + orderRequestDto.getId() + " not found"));

        //Update status
        orders.setOrderStatus(OrderStatus.CANCELLED);
        // Save Order
        Orders saveOrder = orderRepository.save(orders);

        // return response
        return modelMapper.map(saveOrder, OrderRequestDto.class);
    }

    public void restockFallback(OrderRequestDto orderRequestDto, Throwable throwable) {

        log.error("Restocking Order Fallback from Order Service: {}", orderRequestDto.getId(), throwable);


    }
}
