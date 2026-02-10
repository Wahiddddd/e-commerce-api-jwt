package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.CartRequestDTO;
import com.market.e_commerce_JWT.dto.CartResponseDTO;
import com.market.e_commerce_JWT.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(
            @RequestHeader("X-Buyer-Id") String buyerId,
            @RequestBody CartRequestDTO dto) {
        // Langsung lempar ID ke service, biarkan service yang mencari ke DB
        return ResponseEntity.ok(cartService.addToCart(buyerId, dto));
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> getMyCart(
            @RequestHeader("X-Buyer-Id") String buyerId) {
        return ResponseEntity.ok(cartService.findByBuyer(buyerId));
    }
}
