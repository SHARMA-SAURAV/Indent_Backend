package com.example.demo.security;

//package com.indentmanagement.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

//   @Autowired
//private CorsConfigurationSource corsConfigurationSource;
    @Autowired
    JwtAuthenticationEntryPoint point;
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf->csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/indent/create").permitAll()
//
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(ex->ex.authenticationEntryPoint(point))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
////        http.addFilterBefore(filter,User)
//        return http.build();
//    }




@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(c->c.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/auth/users/by-role").permitAll() // ✅ Correct pattern
                    .requestMatchers("/api/indent/create").permitAll()
                    .requestMatchers("/api/auth/me").authenticated()
                    .requestMatchers("/api/indent/fla/pending").authenticated() //  No path variable
                    .requestMatchers("/api/indent/fla/approve").authenticated() // No path variable
                    .requestMatchers("/api/indent/sla/approve").authenticated() //  No path variable
                    .requestMatchers("/api/indent/sla/pending").authenticated() //  No path variable
                    .requestMatchers("/api/indent/store/approve").authenticated() //  No path variable
                    .requestMatchers("/api/indent/store/pending").authenticated() //  No path variable
                    .requestMatchers("/api/indent/finance/approve").authenticated() //  No path variable
                    .requestMatchers("/api/indent/finance/pending").authenticated() //  No path variable
                    .requestMatchers("/api/indent/purchase/pending").authenticated()
                    .requestMatchers("/api/indent/purchase/complete").authenticated()
                    .requestMatchers("/api/indent/user/inspect").hasRole("USER")
                    .requestMatchers("/api/indent/purchase/generate-gfr").hasRole("PURCHASE")
                    .requestMatchers("/api/indent/finance/pay").hasRole("FINANCE")
                    .requestMatchers("/api/indent/user/all").hasRole("USER")


                    .requestMatchers("/api/indent/{indentId}/assign-sla/{slaId}").hasRole("FLA")
                    .requestMatchers("/api/indent/{indentId}/forward-store").hasRole("SLA")
                    .requestMatchers("/api/indent/{indentId}/forward-finance").hasRole("STORE")
                    .requestMatchers("/api/indent/{indentId}/forward-purchase").hasRole("FINANCE")
                    .requestMatchers("/api/indent/{indentId}/complete").hasRole("PURCHASE")
                    .requestMatchers("/api/indent/user/{userId}").hasRole("USER")
                    .requestMatchers("/api/indent/fla/{flaId}").hasRole("FLA")
                    .requestMatchers("/api/indent/sla/{slaId}").hasRole("SLA")
                    .requestMatchers("/api/indent/{indentId}/add-remark").permitAll()

                    .anyRequest().authenticated() // Secure all other endpoints
            )

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(point) // Proper error handling
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(provider);
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173","https://localhost:8080"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

