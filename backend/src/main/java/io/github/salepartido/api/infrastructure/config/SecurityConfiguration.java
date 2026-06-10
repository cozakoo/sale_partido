package io.github.salepartido.api.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import io.github.salepartido.api.security.JwtAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${SPRING_CORS:http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    @SuppressWarnings("squid:S4502")
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .cors(Customizer.withDefaults())
            // CSRF deshabilitado intencionalmente: esta aplicación expone una API REST
            // sin sesiones (SessionCreationPolicy.STATELESS) y usa autenticación por
            // tokens (JWT). No se usan cookies de sesión para la autenticación, por
            // lo que el riesgo de CSRF es mitigado. Rehabilitar CSRF si se añaden
            // formularios basados en sesión o autenticación por cookie.
            .csrf(csrf -> csrf.disable()) // NOSONAR - justified: stateless JWT API, no session cookies
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll())
            .httpBasic(basic -> basic.disable());

        try {
            return http.build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to build SecurityFilterChain", ex);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
