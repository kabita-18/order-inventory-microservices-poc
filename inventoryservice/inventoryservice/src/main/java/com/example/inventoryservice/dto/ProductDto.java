package com.example.inventoryservice.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String price;
    private Integer stock;
}
