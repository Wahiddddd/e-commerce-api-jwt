package com.market.e_commerce_JWT.service;

import com.market.e_commerce_JWT.dto.UserRequestDTO;
import com.market.e_commerce_JWT.dto.UserResponseDTO;
import com.market.e_commerce_JWT.entity.Role;
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

        // 1. Cek username sudah ada
        userRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new BusinessException("Username '" + dto.getUsername() + "' sudah terdaftar!");
        });

        // 2. VALIDASI ROLE
        if (dto.getRole() == null) {
            throw new BusinessException("Role wajib diisi: BUYER atau SELLER");
        }

        Role role;
        try {
            role = Role.valueOf(dto.getRole().toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Role hanya boleh BUYER atau SELLER");
        }

        // 3. Mapping
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User dengan ID tersebut tidak ditemukan"));
        return mapToResponse(user);
    }

    // Helper untuk mapping Entity ke Response DTO
    private UserResponseDTO mapToResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }

}
