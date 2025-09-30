package com.example.demo.Controller;

import com.example.demo.Models.User;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

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

    
}
