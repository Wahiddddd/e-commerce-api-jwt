package com.market.e_commerce_JWT.dto;

import lombok.Data;

@Data
public class CartResponseDTO {
    private String cartId;
    private String productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double subTotal; // (productPrice * quantity)
}
