package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.OrderRequestDto;
import com.example.inventoryservice.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAllInventory();

    ProductDto getProductById(Long id);

    Double reduceStocks(OrderRequestDto orderRequestDto);

    void restock(OrderRequestDto orderRequestDto);
}
