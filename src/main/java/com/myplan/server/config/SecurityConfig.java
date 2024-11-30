package com.myplan.server.config;

import com.myplan.server.jwt.CustomLogoutFilter;
import com.myplan.server.jwt.JwtFilter;
import com.myplan.server.util.JwtUtil;
import com.myplan.server.jwt.LoginFilter;
import com.myplan.server.repository.RefreshRepository;
import com.myplan.server.service.RefreshService;
import com.myplan.server.util.CookieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RefreshRepository refreshRepository;
    private final RefreshService refreshService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    private static final String[] AUTH_LIST={
        "/api/**","/api-docs/**","/swagger-ui/**","/swagger-config,/v3/**"
    };

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, RefreshRepository refreshRepository, RefreshService refreshService, CookieUtil cookieUtil, JwtUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshRepository = refreshRepository;
        this.refreshService = refreshService;
        this.cookieUtil = cookieUtil;
        this.jwtUtil = jwtUtil;
    }

    // 인증 관리자
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> {
            authorize
                    .requestMatchers(AUTH_LIST).permitAll()
                    .requestMatchers("/login", "/api/users/login", "/").permitAll()
                    .requestMatchers(request ->
                            request.getMethod().equals("PATCH") && request.getRequestURI().matches("^/api/users(/.*)?$")
                    ).permitAll() // /api/users/** 에 대한 PATCH 요청을 통과 시킴
                    .anyRequest().authenticated();
        });
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        http.addFilterBefore(new JwtFilter(refreshService, jwtUtil), LoginFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), refreshRepository, cookieUtil, jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
