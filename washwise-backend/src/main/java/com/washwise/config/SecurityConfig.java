package com.washwise.config;

import com.washwise.security.JwtAuthenticationEntryPoint;
import com.washwise.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(authz -> authz
                    // Public auth endpoints
                    .requestMatchers("/api/v1/auth/health").permitAll()
                    .requestMatchers("/api/v1/auth/register").permitAll()
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    
                    // Public service endpoints (GET only)
                    .requestMatchers("GET", "/api/v1/services").permitAll()
                    .requestMatchers("GET", "/api/v1/services/**").permitAll()
                    
                    // Admin-only service endpoints
                    .requestMatchers("POST", "/api/v1/services").hasRole("ADMIN")
                    .requestMatchers("PUT", "/api/v1/services/**").hasRole("ADMIN")
                    .requestMatchers("DELETE", "/api/v1/services/**").hasRole("ADMIN")
                    
                    // Order endpoints (authenticated users)
                    .requestMatchers("GET", "/api/v1/orders").authenticated()
                    .requestMatchers("GET", "/api/v1/orders/**").authenticated()
                    .requestMatchers("POST", "/api/v1/orders").authenticated()
                    .requestMatchers("PUT", "/api/v1/orders/**").authenticated()
                    .requestMatchers("DELETE", "/api/v1/orders/**").hasRole("ADMIN")

                    // Everything else requires auth
                    .anyRequest().authenticated()
            )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        return http.build();
    }
}