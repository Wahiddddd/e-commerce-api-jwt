package com.market.e_commerce_JWT.service;

import com.market.e_commerce_JWT.dto.UserRequestDTO;
import com.market.e_commerce_JWT.dto.UserResponseDTO;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.exception.BusinessException;
import com.market.e_commerce_JWT.exception.ResourceNotFoundException;
import com.market.e_commerce_JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO dto) {
        // 1. Validasi: Cek apakah username sudah ada
        userRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new BusinessException("Username '" + dto.getUsername() + "' sudah terdaftar!");
        });

        // 2. Mapping DTO ke Entity
        User user = new User();
        user.setUsername(dto.getUsername());
        // ENKRIPSI PASSWORD DI SINI
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole().toUpperCase()); // Pastikan uppercase (BUYER/SELLER)

        User savedUser = userRepository.save(user);

        // 3. Mapping Entity ke Response DTO
        return mapToResponse(savedUser);
    }

    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User dengan ID tersebut tidak ditemukan"));
        return mapToResponse(user);
    }

    // Helper untuk mapping Entity ke Response DTO
    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }
}
