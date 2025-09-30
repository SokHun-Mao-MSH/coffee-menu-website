package com.example.demo.Controller;

import com.example.demo.Models.User;
import com.example.demo.Repositories.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Models.Order;
import com.example.demo.Repositories.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show Profile
    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        String username = authentication.getName(); 
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "admin-profile"; 
    }

    // Show Edit Form
    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "admin-profile-edit";
    }

    // Handle Update
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Authentication authentication, Model model) {
        String username = authentication.getName();
        User existingUser = userRepository.findByUsername(username);

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        // existingUser.setRole(updatedUser.getRole());
        // existingUser.setPassword(updatedUser.getPassword());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        userRepository.save(existingUser);
        model.addAttribute("user", existingUser);
        model.addAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/admin/profile?success";
    }
    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public String listAllOrders(Model model) {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
        model.addAttribute("orders", orders);
        return "list-order";
    }

    @GetMapping("/orders/{id}")
    @Transactional(readOnly = true)
    public String viewOrderDetails(@PathVariable("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + orderId));
        model.addAttribute("order", order);
        return "customer-order-details";
    }

    
}
