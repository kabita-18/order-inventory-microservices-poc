package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequestDto {

    private Long id;
    private List<OrderRequestItemDto> items;
    private BigDecimal totalPrice;
}
