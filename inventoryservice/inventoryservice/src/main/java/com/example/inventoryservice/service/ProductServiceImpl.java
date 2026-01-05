package com.example.inventoryservice.service;
import com.example.inventoryservice.dto.OrderRequestDto;
import com.example.inventoryservice.dto.OrderRequestItemDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ProductDto> getAllInventory() {
        log.info("In ProductServiceImpl.getAllInventory()");
        List<Product> inventories = productRepository.findAll();
        return inventories.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public ProductDto getProductById(Long id) {
        log.info("In ProductServiceImpl.getProductById(){} with id:", id);
        Optional<Product> inventory = productRepository.findById(id);
        return inventory.map(item -> modelMapper.map(item, ProductDto.class))
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    @Override
    @Transactional
    public Double reduceStocks(OrderRequestDto orderRequestDto) {
        log.info("Reducing the Stock");
        Double totalPrice = 0.0;
        for(OrderRequestItemDto orderRequestItemDto : orderRequestDto.getItems()) {
            Long productId = orderRequestItemDto.getProductId();
            Long quantity = orderRequestItemDto.getQuantity();

            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with id:" + productId));

            if(product.getStock() < quantity) {
                throw new RuntimeException("Quantity exceeded and can not fulfilled for given quantity");
            }
            product.setStock((int) (product.getStock()- quantity));
            productRepository.save(product);

            totalPrice += quantity*product.getStock();

        }
        return totalPrice;
    }

    @Transactional
    @Override
    public void restock(OrderRequestDto orderRequestDto) {
        log.info("In ProductServiceImpl.restock(){} with id:", orderRequestDto);
        long updatedQuantity = 0;
        for(OrderRequestItemDto orderRequestItemDto : orderRequestDto.getItems()) {
            Long productId = orderRequestItemDto.getProductId();
            Long restockQty = orderRequestItemDto.getQuantity();

            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with id:" + productId));
            if(restockQty < 0) {
                throw new RuntimeException("Quantity must be greater than 0");
            }
            updatedQuantity = product.getStock()+restockQty;
            product.setStock((int) updatedQuantity);
            productRepository.save(product);
        }

    }


}
