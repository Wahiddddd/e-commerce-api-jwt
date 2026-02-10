package com.market.e_commerce_JWT.controller;

import com.market.e_commerce_JWT.dto.DeleteResponse;
import com.market.e_commerce_JWT.dto.ProductRequestDTO;
import com.market.e_commerce_JWT.dto.ProductResponseDTO;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private com.market.e_commerce_JWT.service.FileStorageService fileStorageService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponseDTO> addProduct(
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {

        User user = (User) authentication.getPrincipal();
        String sellerId = user.getId();

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStock(stock);

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.save(image);
            dto.setImageUrl("/uploads/" + fileName);
        }

        return ResponseEntity.ok(productService.createProduct(dto, sellerId));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {

        User user = (User) authentication.getPrincipal();
        String sellerId = user.getId();

        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStock(stock);

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.save(image);
            dto.setImageUrl("/uploads/" + fileName);
        }

        return ResponseEntity.ok(productService.updateProduct(id, dto, sellerId));
    }

    @GetMapping("/my-products")
    public ResponseEntity<Page<ProductResponseDTO>> getMyProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(productService.getProductsBySeller(user.getId(), page, size));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteProduct(
            @PathVariable String id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        productService.deleteProduct(id, user.getId());
        return ResponseEntity.ok(new DeleteResponse("Produk berhasil dihapus (Soft Delete)", id));
    }
}
