package com.example.inventoryservice.dto;

import lombok.Data;

@Data
public class OrderRequestItemDto {
    private Long productId;
    private Long quantity;
}
