package com.example.demo.Security;

import com.example.demo.Models.CartItemDto;
import com.example.demo.Models.User;
import com.example.demo.Service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OrderService orderService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            @SuppressWarnings("unchecked")
            List<CartItemDto> pendingCart = (List<CartItemDto>) session.getAttribute("pendingCart");

            if (pendingCart != null && !pendingCart.isEmpty()) {
                User loggedInUser = (User) authentication.getPrincipal();
                try {
                    orderService.createOrder(loggedInUser, pendingCart);
                    session.removeAttribute("pendingCart");
                    response.sendRedirect("/order/success?clear=true");
                    return;
                } catch (Exception e) {
                    System.err.println("Error processing pending cart after login: " + e.getMessage());
                }
            }
        }

        var roles = authentication.getAuthorities();
        if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_MANAGER"))) {
            response.sendRedirect("/dashboard");
        } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_CUSTOMER") || r.getAuthority().equals("ROLE_USER"))) {
            response.sendRedirect("/index");
        } else {
            response.sendRedirect("/home");
        }
    }
}