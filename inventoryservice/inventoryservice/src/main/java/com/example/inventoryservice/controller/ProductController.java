package com.example.inventoryservice.controller;

import com.example.inventoryservice.clients.OrderOpenFeignClient;
import com.example.inventoryservice.dto.OrderRequestDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private  final ProductService productService;
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;
    private final OrderOpenFeignClient orderOpenFeignClient;

    @GetMapping("/fetchOrder")
    public String fetchFromOrdersService(){
        List<ServiceInstance> instances =
                discoveryClient.getInstances("order-service");

        if (instances.isEmpty()) {
            throw new RuntimeException("Order service not available");
        }

        ServiceInstance orderService = instances.get(0);

//        return restClient.get()
//                .uri(orderService.getUri()+ "/order/core/helloOrders")
//                .retrieve()
//                .body(String.class);

        return orderOpenFeignClient.helloOrders();

    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> inventories = productService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getInventoryById(@PathVariable Long id){
        ProductDto inventories = productService.getProductById(id);
        return ResponseEntity.ok(inventories);
    }

    @PutMapping("/reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto  orderRequestDto){
        Double totalPrice = productService.reduceStocks(orderRequestDto);
        return ResponseEntity.ok(totalPrice);
    }

    @PutMapping("/restock")
    public ResponseEntity<Void> restock(@RequestBody OrderRequestDto  orderRequestDto){
         productService.restock(orderRequestDto);
        return ResponseEntity.ok().build();
    }


}
