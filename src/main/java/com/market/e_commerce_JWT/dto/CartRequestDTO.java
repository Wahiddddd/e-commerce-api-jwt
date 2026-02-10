package com.market.e_commerce_JWT.dto;

import lombok.Data;

@Data
public class CartRequestDTO {
    private String productId;
    private Integer quantity;
}
