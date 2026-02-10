package com.market.e_commerce_JWT.repository;

import com.market.e_commerce_JWT.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    // Untuk mengecek apakah barang yang sama sudah ada di keranjang
    Optional<Cart> findByBuyerIdAndProductId(String buyerId, String productId);

    // untuk mencari cart berdasarkan buyer id
    List<Cart> findByBuyerId(String buyerId);
}
