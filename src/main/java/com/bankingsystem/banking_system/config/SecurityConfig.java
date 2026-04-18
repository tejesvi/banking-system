package com.bankingsystem.banking_system.config;

import com.bankingsystem.banking_system.filter.JwtFilter;
import com.bankingsystem.banking_system.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()).cors( cors -> {})
                .authorizeHttpRequests(auth -> auth

                        // Public
                        .requestMatchers("/auth/**").permitAll()

                        // ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/accounts/**").hasRole("ADMIN")

                        // USER ACCESS
                        .requestMatchers("/accounts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transactions/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transfers/my-transfers").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transfers/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}