// package com.workschedule.app.config;

// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import
// org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
// import
// org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import
// org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import
// org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import lombok.RequiredArgsConstructor;

// @Configuration
// @RequiredArgsConstructor
// @EnableWebSecurity
// public class SecurityConfig {

// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
// Exception {

// http
// .csrf(csrf -> csrf.disable())
// .authorizeHttpRequests(authorize -> authorize
// .requestMatchers("/api/**").permitAll()
// .anyRequest().permitAll())
// .sessionManagement(session -> session
// .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

// return http.build();
// }

// }
