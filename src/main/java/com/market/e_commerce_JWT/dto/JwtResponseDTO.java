package com.market.e_commerce_JWT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Untuk Response (Kirim Token ke Client)
@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
}
