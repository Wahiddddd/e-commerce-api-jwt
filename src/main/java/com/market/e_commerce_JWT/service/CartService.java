package com.market.e_commerce_JWT.service;

import com.market.e_commerce_JWT.dto.CartRequestDTO;
import com.market.e_commerce_JWT.dto.CartResponseDTO;
import com.market.e_commerce_JWT.entity.Cart;
import com.market.e_commerce_JWT.entity.Product;
import com.market.e_commerce_JWT.entity.Role;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.exception.BusinessException;
import com.market.e_commerce_JWT.exception.ResourceNotFoundException;
import com.market.e_commerce_JWT.repository.CartRepository;
import com.market.e_commerce_JWT.repository.ProductRepository;
import com.market.e_commerce_JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    public CartResponseDTO addToCart(String buyerId, CartRequestDTO dto) {

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer tidak ditemukan"));

        // ================= VALIDASI ROLE =================
        if (!buyer.getRole().equals(Role.BUYER)) {
            throw new BusinessException("Hanya BUYER yang boleh add to cart");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produk dengan ID " + dto.getProductId() + " tidak ditemukan"));

        // ================= VALIDASI QUANTITY =================
        if (dto.getQuantity() <= 0) {
            throw new BusinessException("Quantity minimal 1");
        }

        Optional<Cart> existingCart =
                cartRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId());

        // ================= CEK STOK =================
        int newQty = existingCart.isPresent()
                ? existingCart.get().getQuantity() + dto.getQuantity()
                : dto.getQuantity();

        if (newQty > product.getStock()) {
            throw new BusinessException(
                    "Stok produk tidak mencukupi. Stok tersedia: " + product.getStock()
            );
        }

        Cart cart;

        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(newQty);
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

    @Transactional
    public CartResponseDTO updateQuantity(String buyerId, String cartId, int quantity) {

        if (quantity <= 0) {
            throw new BusinessException("Quantity minimal 1");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer tidak ditemukan"));

        if (!buyer.getRole().equals(Role.BUYER)) {
            throw new BusinessException("Hanya BUYER yang boleh update cart");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item tidak ditemukan"));

        // Pastikan cart milik user yang login
        if (!cart.getBuyer().getId().equals(buyerId)) {
            throw new BusinessException("Tidak boleh mengubah cart milik orang lain");
        }

        Product product = cart.getProduct();

        // Cek stok
        if (quantity > product.getStock()) {
            throw new BusinessException(
                    "Stok produk tidak mencukupi. Stok tersedia: " + product.getStock()
            );
        }

        cart.setQuantity(quantity);

        Cart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteCartItem(String buyerId, String cartId) {

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer tidak ditemukan"));

        if (!buyer.getRole().equals(Role.BUYER)) {
            throw new BusinessException("Hanya BUYER yang boleh hapus cart");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item tidak ditemukan"));

        if (!cart.getBuyer().getId().equals(buyerId)) {
            throw new BusinessException("Tidak boleh menghapus cart milik orang lain");
        }

        cartRepository.delete(cart);
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