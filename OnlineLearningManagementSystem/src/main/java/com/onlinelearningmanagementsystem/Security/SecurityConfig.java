package com.onlinelearningmanagementsystem.Security;

import com.onlinelearningmanagementsystem.JwtService.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
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
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
     return    httpSecurity
        .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Public Endpoints (Register & Login)
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()

                        //Course searcher endpoints
                        .requestMatchers(HttpMethod.GET,
                                "/api/course",
                                "/api/course/category/**",
                                "/api/course/title/**",
                                "/api/course/code/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/course/my").hasRole("INSTRUCTOR")

                        // Assessment endpoints
                        .requestMatchers(HttpMethod.POST, "/api/assessment").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/assessment/**").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/assessment/**").hasRole("INSTRUCTOR")

                        // Course modification
                        .requestMatchers(HttpMethod.PUT, "/api/course/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/course").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/course/**").hasAnyRole("INSTRUCTOR", "ADMIN")

                        // Enrollment endpoints
                        .requestMatchers(HttpMethod.POST, "/api/enroll").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/un_enroll").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/re_enroll").hasAnyRole("STUDENT", "ADMIN")


                        // Submitted Assessments endpoints
                        .requestMatchers(HttpMethod.POST, "/api/submitted-assessment").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/submitted-assessment").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/submitted-assessment").hasAnyRole("STUDENT", "INSTRUCTOR")
                        .requestMatchers("/api/submitted-assessment/**").hasRole("INSTRUCTOR")

                        .requestMatchers("/ws").permitAll()
                        .anyRequest().authenticated()

                ).userDetailsService(userDetailsService)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();



    }
}

