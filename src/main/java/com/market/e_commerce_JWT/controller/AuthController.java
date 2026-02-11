package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.JwtResponse;
import com.market.e_commerce_JWT.dto.LoginRequest;
import com.market.e_commerce_JWT.dto.UserRequestDTO;
import com.market.e_commerce_JWT.dto.UserResponseDTO;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.security.JwtUtils;
import com.market.e_commerce_JWT.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(
            @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(loginRequest.getUsername());

        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getRole().name()   // ← karena sekarang ENUM
                )
        );
    }

    // ===================== REGISTER =====================
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRequestDTO signUpRequest) {

        UserResponseDTO user = userService.createUser(signUpRequest);

        return ResponseEntity.ok(user);
    }
}

