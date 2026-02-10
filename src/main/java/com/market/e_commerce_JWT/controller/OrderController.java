package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.OrderResponseDTO;
import com.market.e_commerce_JWT.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(
            @RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(orderService.checkout(buyerId));
    }
}
