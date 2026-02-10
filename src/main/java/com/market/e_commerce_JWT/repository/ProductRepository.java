package com.market.e_commerce_JWT.repository;

// Hapus import Page dan Pageable yang lama, ganti dengan ini:
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.market.e_commerce_JWT.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // Ini sudah cukup untuk meng-cover pencarian produk per seller + pagination +
    // sorting
    Page<Product> findBySellerId(String sellerId, Pageable pageable);
}
