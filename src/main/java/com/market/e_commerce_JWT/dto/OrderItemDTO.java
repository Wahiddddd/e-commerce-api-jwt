package com.market.e_commerce_JWT.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productName;
    private Integer quantity;
    private Double priceAtPurchase;
}
