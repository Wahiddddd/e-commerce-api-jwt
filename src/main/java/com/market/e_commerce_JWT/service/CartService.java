package com.market.e_commerce_JWT.service;

import com.market.e_commerce_JWT.dto.CartRequestDTO;
import com.market.e_commerce_JWT.dto.CartResponseDTO;
import com.market.e_commerce_JWT.entity.Cart;
import com.market.e_commerce_JWT.entity.Product;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.exception.ResourceNotFoundException;
import com.market.e_commerce_JWT.repository.CartRepository;
import com.market.e_commerce_JWT.repository.ProductRepository;
import com.market.e_commerce_JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    public CartResponseDTO addToCart(String buyerId, CartRequestDTO dto) {
        // Menerapkan Exception jika produk tidak ditemukan
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer tidak ditemukan"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + dto.getProductId() + " tidak ditemukan"));

        Optional<Cart> existingCart = cartRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId());

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + dto.getQuantity());
        } else {
            cart = new Cart();
            cart.setBuyer(buyer);
            cart.setProduct(product);
            cart.setQuantity(dto.getQuantity());
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    // Method untuk menampilkan semua isi keranjang buyer
    public List<CartResponseDTO> findByBuyer(String buyerId) {
        List<Cart> carts = cartRepository.findByBuyerId(buyerId);

        // Jika keranjang kosong, kita bisa pilih return list kosong atau lempar exception
        // Di sini kita return list kosong agar tidak error di frontend saat keranjang baru dibuat
        return carts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper untuk mapping Entity ke Response DTO
    private CartResponseDTO mapToResponse(Cart cart) {
        CartResponseDTO res = new CartResponseDTO();
        res.setCartId(cart.getId());
        res.setProductId(cart.getProduct().getId());
        res.setProductName(cart.getProduct().getName());
        res.setProductPrice(cart.getProduct().getPrice());
        res.setQuantity(cart.getQuantity());
        // Menghitung subtotal otomatis di sisi server
        res.setSubTotal(cart.getProduct().getPrice() * cart.getQuantity());
        return res;
    }
}