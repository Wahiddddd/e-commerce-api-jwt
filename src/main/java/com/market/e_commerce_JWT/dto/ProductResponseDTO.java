package com.market.e_commerce_JWT.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductResponseDTO {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String sellerName;
    private LocalDateTime createdAt;
}
