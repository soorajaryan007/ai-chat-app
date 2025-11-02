package com.example.aibackend.config;

import com.example.aibackend.service.CustomOidcUserService;
import com.example.aibackend.service.CustomOAuth2UserService; // Import this
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
    private final CustomOAuth2UserService customOAuth2UserService; // Add this

    // This constructor now accepts BOTH user services
    public SecurityConfig(CustomOidcUserService customOidcUserService, CustomOAuth2UserService customOAuth2UserService) {
        this.customOidcUserService = customOidcUserService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    /**
     * This bean tells Spring Security to completely IGNORE requests for static
     * files. This is the fix for your styling.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                new AntPathRequestMatcher("/*.css"), // Matches style.css
                new AntPathRequestMatcher("/*.js"),  // Matches chat.js
                new AntPathRequestMatcher("/*.svg")  // Matches google-logo.svg & github-logo.svg
            );
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // This disables CSRF protection for your API, which is
            // needed for your JavaScript 'fetch' to work.
            .csrf(csrf -> 
                csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))
            )
            .authorizeHttpRequests(authorize -> authorize
                // 1. Allow public access to the login page ONLY.
                .requestMatchers("/login").permitAll()
                
                // 2. All other requests (like "/" or "/chat/*")
                //    MUST be authenticated.
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // On success, go to the lobby
                .userInfoEndpoint(userInfo -> userInfo
                    // --- THIS IS THE CORRECTED BLOCK ---
                    .oidcUserService(this.customOidcUserService)   // For Google (OIDC)
                    .userService(this.customOAuth2UserService)     // For GitHub (OAuth2)
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login") // On logout, go back to login
                .permitAll()
            );

        return http.build();
    }
}