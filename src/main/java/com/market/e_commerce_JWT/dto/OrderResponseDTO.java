package com.market.e_commerce_JWT.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private String orderId;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items; // Detail barang yang dibeli
}
