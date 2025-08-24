package com.dieti.dietiestatesbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import com.dieti.dietiestatesbackend.security.JwtAuthenticationFilter;
import com.dieti.dietiestatesbackend.service.PasswordValidator;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String AUTH_ENDPOINTS = "/auth/**";
    private static final String OAUTH2_ENDPOINTS = "/oauth2/**";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String SIGNUP_ENDPOINT = "/signup";
    private static final String REFRESH_ENDPOINT = "/refresh";
    private static final String SWAGGER_API_DOCS = "/v3/api-docs/**";
    private static final String SWAGGER_UI = "/swagger-ui/**";
    private static final String SWAGGER_UI_HTML = "/swagger-ui.html";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    AUTH_ENDPOINTS,
                    OAUTH2_ENDPOINTS,
                    LOGIN_ENDPOINT,
                    SIGNUP_ENDPOINT,
                    REFRESH_ENDPOINT,
                    SWAGGER_API_DOCS,
                    SWAGGER_UI,
                    SWAGGER_UI_HTML
                ).permitAll()
                .anyRequest().authenticated()
            )
            // .oauth2Login(Customizer.withDefaults())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // Disable the default Spring Security logout filter so that the application's
            // `/logout` controller can handle logout logic and return JSON/status instead
            // of performing an HTTP redirect to the login page.
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return bCryptPasswordEncoder();
    }

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}