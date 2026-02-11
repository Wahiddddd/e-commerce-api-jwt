package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.CartRequestDTO;
import com.market.e_commerce_JWT.dto.CartResponseDTO;
import com.market.e_commerce_JWT.entity.Role;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.exception.BusinessException;
import com.market.e_commerce_JWT.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ========== ADD ==========
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(
            Authentication authentication,
            @RequestBody CartRequestDTO dto) {

        User buyer = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.addToCart(buyer.getId(), dto)
        );
    }

    // ========== GET MY CART ==========
    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> getMyCart(
            Authentication authentication) {

        User buyer = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.findByBuyer(buyer.getId())
        );
    }

    // ========== UPDATE QUANTITY ==========
    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            Authentication authentication,
            @PathVariable String cartId,
            @RequestParam int quantity) {

        User buyer = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.updateQuantity(buyer.getId(), cartId, quantity)
        );
    }

    // ========== DELETE ITEM ==========
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCartItem(
            Authentication authentication,
            @PathVariable String cartId) {

        User buyer = (User) authentication.getPrincipal();

        cartService.deleteCartItem(buyer.getId(), cartId);

        return ResponseEntity.ok(Map.of(
                "message", "Item cart berhasil dihapus"
        ));
    }
}

