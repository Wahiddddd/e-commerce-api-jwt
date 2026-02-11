package com.market.e_commerce_JWT.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // 🔥 WAJIB untuk @PreAuthorize
public class WebSecurityConfig {

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // PUBLIC
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/uploads/**",
                                "/actuator/**"
                        ).permitAll()

                        // sisanya wajib login
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
                "status": 401,
                "message": "Unauthorized - Silakan login dulu"
            }
            """);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
                "status": 403,
                "message": "Anda tidak memiliki akses ke resource ini"
            }
            """);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

