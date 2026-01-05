package com.example.inventoryservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name= "order-service", path = "/order")
public interface OrderOpenFeignClient {


    @GetMapping("/core/helloOrders")
    String helloOrders();
}
