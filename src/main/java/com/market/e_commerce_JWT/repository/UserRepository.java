package com.market.e_commerce_JWT.repository;

import com.market.e_commerce_JWT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Digunakan untuk cek role saat login/simulasi
    Optional<User> findByUsername(String username);
}
