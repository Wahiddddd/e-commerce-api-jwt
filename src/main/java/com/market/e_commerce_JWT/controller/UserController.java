package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.UserResponseDTO;
import com.market.e_commerce_JWT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
