package com.example.demo.Controller;

import com.example.demo.Models.CartItemDto;
import com.example.demo.Models.User;
import com.example.demo.Service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> processCheckout(@RequestBody List<CartItemDto> cartItems, @AuthenticationPrincipal User customer, HttpSession session) {
        if (customer == null) {
            // User is not logged in. Store the cart in the session and ask them to sign in.
            session.setAttribute("pendingCart", cartItems);
            return ResponseEntity.status(401).body("{\"redirectUrl\": \"/signin\"}");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        try {
            orderService.createOrder(customer, cartItems);
            return ResponseEntity.ok().body("{\"redirectUrl\": \"/order/success\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing order: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "order-success";
    }
}