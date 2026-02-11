package com.market.e_commerce_JWT.service;

import com.market.e_commerce_JWT.dto.ProductRequestDTO;
import com.market.e_commerce_JWT.dto.ProductResponseDTO;
import com.market.e_commerce_JWT.entity.Product;
import com.market.e_commerce_JWT.entity.Role;
import com.market.e_commerce_JWT.entity.User;
import com.market.e_commerce_JWT.exception.BusinessException;
import com.market.e_commerce_JWT.exception.ResourceNotFoundException;
import com.market.e_commerce_JWT.repository.ProductRepository;
import com.market.e_commerce_JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public ProductResponseDTO createProduct(ProductRequestDTO dto, String sellerId) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller tidak ditemukan"));

        // VALIDASI
        if (seller.getRole() != Role.SELLER) {
            throw new BusinessException("Hanya akun dengan Role SELLER yang bisa menambah produk");
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setSeller(seller);

        Product savedProduct = productRepository.save(product);

        return mapToProductResponse(savedProduct);
    }

    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Mengubah Page<Product> menjadi Page<ProductResponseDTO>
        return productRepository.findAll(pageable).map(this::mapToProductResponse);
    }

    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));
        return mapToProductResponse(product);
    }

    public ProductResponseDTO updateProduct(String id, ProductRequestDTO dto, String sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));

        // Validasi: hanya seller pemilik produk yang bisa edit
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new BusinessException("Anda tidak memiliki izin untuk mengubah produk ini");
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    public void deleteProduct(String id, String sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));

        // Validasi: hanya seller pemilik produk yang bisa hapus
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new BusinessException("Anda tidak memiliki izin untuk menghapus produk ini");
        }

        // Karena @SQLDelete sudah ada di Entity, kita tetap panggil delete repository
        // Hibernate akan mengeksekusi UPDATE instead of DELETE
        productRepository.delete(product);
    }

    public Page<ProductResponseDTO> getProductsBySeller(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findBySellerId(sellerId, pageable).map(this::mapToProductResponse);
    }

    // Helper method untuk Mapping Entity -> DTO
    private ProductResponseDTO mapToProductResponse(Product p) {
        ProductResponseDTO res = new ProductResponseDTO();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setDescription(p.getDescription());
        res.setPrice(p.getPrice());
        res.setStock(p.getStock());
        res.setImageUrl(p.getImageUrl());
        res.setSellerName(p.getSeller().getUsername()); // Hanya ambil nama seller
        res.setCreatedAt(p.getCreatedAt());
        return res;
    }
}
