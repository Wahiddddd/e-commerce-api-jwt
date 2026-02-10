package com.market.e_commerce_JWT.service;

import com.market.e_commerce_JWT.dto.OrderItemDTO;
import com.market.e_commerce_JWT.dto.OrderResponseDTO;
import com.market.e_commerce_JWT.entity.*;
import com.market.e_commerce_JWT.exception.BusinessException;
import com.market.e_commerce_JWT.exception.ResourceNotFoundException;
import com.market.e_commerce_JWT.repository.CartRepository;
import com.market.e_commerce_JWT.repository.OrderRepository;
import com.market.e_commerce_JWT.repository.ProductRepository;
import com.market.e_commerce_JWT.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public OrderResponseDTO checkout(String buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer dengan ID tersebut tidak ditemukan"));

        List<Cart> cartItems = cartRepository.findByBuyerId(buyer.getId());
        if (cartItems.isEmpty()) throw new BusinessException("Gagal checkout: Keranjang belanja kamu masih kosong!");

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (Cart cart : cartItems) {
            Product product = cart.getProduct();
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException("Stok produk '" + product.getName() + "' tidak mencukupi");
            }

            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(cart.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            orderItems.add(item);

            total += product.getPrice() * cart.getQuantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteAll(cartItems);

        return mapToOrderResponse(savedOrder);
    }

    private OrderResponseDTO mapToOrderResponse(Order o) {
        OrderResponseDTO res = new OrderResponseDTO();
        res.setOrderId(o.getId());
        res.setTotalAmount(o.getTotalAmount());
        res.setOrderDate(o.getOrderDate());

        List<OrderItemDTO> items = o.getOrderItems().stream().map(item -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setProductName(item.getProduct().getName());
            i.setQuantity(item.getQuantity());
            i.setPriceAtPurchase(item.getPriceAtPurchase());
            return i;
        }).collect(Collectors.toList());

        res.setItems(items);
        return res;
    }
}
