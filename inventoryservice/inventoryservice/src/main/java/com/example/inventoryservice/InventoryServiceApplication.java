package com.example.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients
@EnableKafka

public class InventoryServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(InventoryServiceApplication.class, args);
        System.out.println("Inventory Service Application Started on 8081");
	}

}
