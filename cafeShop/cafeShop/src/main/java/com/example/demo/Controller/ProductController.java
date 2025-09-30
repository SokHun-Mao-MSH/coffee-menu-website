package com.example.demo.Controller;

import com.example.demo.Models.Product;
import com.example.demo.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // list all products
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "dashboard";
    }

    // Form
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        Product product = new Product(); 
        model.addAttribute("product", product);
        return "add-edit-product";
    }

    // Show form
    @GetMapping("/products/editproduct")
    public String editProduct(
        @RequestParam("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + id));
        model.addAttribute("product", product);
        return "add-edit-product"; 
    }

    // Save or Update Product
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productRepository.save(product);
        return "redirect:/dashboard";
    }

    // Delete Product
    @GetMapping("/products/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/dashboard";
    }
}
