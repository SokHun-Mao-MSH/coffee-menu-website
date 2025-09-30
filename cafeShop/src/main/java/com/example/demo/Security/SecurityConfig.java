package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.Service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class SecurityConfig {

    private final UserService userService;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(UserService userService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userService = userService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authenticationProvider(authenticationProvider()) // Register the custom authentication provider
            .authorizeHttpRequests(auth -> auth
                // For admin/manager 
                .requestMatchers("/dashboard/**", "/admin/**", "/users/**").hasAnyRole("ADMIN", "MANAGER")
                // for user 
                .requestMatchers("/index", "/customer/**", "/order/success").hasAnyRole("USER", "CUSTOMER", "ADMIN", "MANAGER")
                // public access to guest pages 
                .requestMatchers("/", "/home", "/signup", "/signin", "/signout", "/cart", "/order/checkout", "/css/**", "/javascript/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/signin") 
                .loginProcessingUrl("/signin") 
                .successHandler(customAuthenticationSuccessHandler) 
                .failureUrl("/signin?error=true") 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/perform_logout") 
                .logoutSuccessUrl("/signin?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return httpSecurity.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
