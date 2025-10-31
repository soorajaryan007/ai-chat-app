package com.example.aibackend.config;

import com.example.aibackend.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    /**
     * This bean tells Spring Security to COMPLETELY IGNORE
     * requests for static files. This is the fix for your styling.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                new AntPathRequestMatcher("/*.css"), // Matches style.css
                new AntPathRequestMatcher("/*.js"),  // Matches chat.js
                new AntPathRequestMatcher("/*.svg")  // Matches google-logo.svg
            );
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // --- THIS IS THE FIX FOR THE "LOGIN LOOP" ---
            // We must disable CSRF protection for our API paths
            // so JavaScript 'fetch' requests can post data.
            .csrf(csrf -> 
                csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))
            )
            .authorizeHttpRequests(authorize -> authorize
                // 1. Allow public access to the login page ONLY.
                //    The lobby "/" is now protected.
                .requestMatchers("/login").permitAll()
                
                // 2. All other requests (like "/" or "/chat/*")
                //    MUST be authenticated.
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // On success, go to the lobby
                .userInfoEndpoint(userInfo ->
                    userInfo.oidcUserService(this.customOidcUserService)
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login") // On logout, go back to login
                .permitAll()
            );

        return http.build();
    }
}