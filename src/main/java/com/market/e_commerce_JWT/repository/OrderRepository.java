package com.market.e_commerce_JWT.repository;

import com.market.e_commerce_JWT.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // Mencari semua order berdasarkan ID Buyer (untuk fitur Riwayat Pesanan)
    List<Order> findByBuyerIdOrderByOrderDateDesc(String buyerId);
}
