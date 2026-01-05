package com.example.orderservice.clients;

import com.example.orderservice.dto.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryOpenFeignClient {

    @PutMapping("/products/reduce-stocks")
    double reduceStocks(@RequestBody OrderRequestDto orderRequestDto);

    @PutMapping("products/restock")
    void restock (@RequestBody OrderRequestDto orderRequestDto);
}
