package com.example.demo.Service;

import com.example.demo.Models.CartItemDto;
import com.example.demo.Models.Order;
import com.example.demo.Models.OrderItem;
import com.example.demo.Models.Product;
import com.example.demo.Models.OrderStatus;
import com.example.demo.Models.User;
import com.example.demo.Repositories.OrderRepository;
import com.example.demo.Repositories.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(User customer, List<CartItemDto> cartItems) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (CartItemDto itemDto : cartItems) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemDto.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setSize(itemDto.getSize());
            orderItem.setNote(itemDto.getNote());
            orderItem.setPriceAtTimeOfOrder(itemDto.getPrice()); // Use price from cart which includes size modifier

            order.getOrderItems().add(orderItem);

            BigDecimal itemTotalPrice = itemDto.getPrice().multiply(new BigDecimal(itemDto.getQuantity()));
            totalOrderPrice = totalOrderPrice.add(itemTotalPrice);
        }

        order.setTotalPrice(totalOrderPrice);
        return orderRepository.save(order);
    }
}