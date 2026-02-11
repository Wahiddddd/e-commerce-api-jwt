package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.DeleteResponse;
import com.market.e_commerce_JWT.dto.ProductRequestDTO;
import com.market.e_commerce_JWT.dto.ProductResponseDTO;
import com.market.e_commerce_JWT.entity.Role;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.exception.BusinessException;
import com.market.e_commerce_JWT.exception.ResourceNotFoundException;
import com.market.e_commerce_JWT.repository.UserRepository;
import com.market.e_commerce_JWT.service.FileStorageService;
import com.market.e_commerce_JWT.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    // ================= CREATE =================
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponseDTO> addProduct(
            Authentication authentication,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        User user = getCurrentUser(authentication);

        if (user.getRole() != Role.SELLER) {
            throw new BusinessException("Hanya akun SELLER yang bisa menambah produk");
        }

        ProductRequestDTO dto = buildDto(name, description, price, stock, image);

        return ResponseEntity.ok(
                productService.createProduct(dto, user.getId())
        );
    }

    // ================= UPDATE =================
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            Authentication authentication,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        User user = getCurrentUser(authentication);

        ProductRequestDTO dto = buildDto(name, description, price, stock, image);

        return ResponseEntity.ok(
                productService.updateProduct(id, dto, user.getId())
        );
    }

    // ================= MY PRODUCTS =================
    @GetMapping("/my-products")
    public ResponseEntity<Page<ProductResponseDTO>> getMyProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(
                productService.getProductsBySeller(user.getId(), page, size)
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.getAllProducts(page, size)
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteProduct(
            @PathVariable String id,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        productService.deleteProduct(id, user.getId());

        return ResponseEntity.ok(
                new DeleteResponse("Produk berhasil dihapus", id)
        );
    }

    // ================= HELPER =================

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private ProductRequestDTO buildDto(
            String name,
            String description,
            Double price,
            Integer stock,
            MultipartFile image) {

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStock(stock);

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.save(image);
            dto.setImageUrl("/uploads/" + fileName);
        }

        return dto;
    }
}

