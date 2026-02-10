package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.JwtResponse;
import com.market.e_commerce_JWT.dto.LoginRequest;
import com.market.e_commerce_JWT.dto.UserRequestDTO;
import com.market.e_commerce_JWT.dto.UserResponseDTO;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.security.JwtUtils;
import com.market.e_commerce_JWT.service.UserService;
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

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(loginRequest.getUsername());

        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO signUpRequest) {
        UserResponseDTO user = userService.createUser(signUpRequest);
        return ResponseEntity.ok(user);
    }
}
