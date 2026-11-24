package com.campuserp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Role-based access, kept deliberately simple for learning:
 *
 *   admin    / admin123    — ROLE_ADMIN    — full access (create/edit/delete everything)
 *   faculty  / faculty123  — ROLE_FACULTY  — read everything; create attendance &amp; timetable
 *   student  / student123  — ROLE_STUDENT  — read-only
 *
 * Demo users live in memory (no user table yet). In a real ERP these
 * would come from a database + a proper login page with sessions/JWT.
 * HTTP Basic is used so curl and the simple JS frontend can log in
 * with just a username/password — no token plumbing needed.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123")).roles("ADMIN").build();
        UserDetails faculty = User.withUsername("faculty")
                .password(encoder.encode("faculty123")).roles("FACULTY").build();
        UserDetails student = User.withUsername("student")
                .password(encoder.encode("student123")).roles("STUDENT").build();
        return new InMemoryUserDetailsManager(admin, faculty, student);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // REST API + Basic auth: no browser form/CSRF token
            .authorizeHttpRequests(auth -> auth
                // Frontend + H2 console are public; the API itself is protected.
                .requestMatchers("/", "/index.html", "/app.js", "/styles.css",
                        "/h2-console/**").permitAll()
                // Writes to students/fees/inventory: admin only.
                .requestMatchers(HttpMethod.POST, "/api/students/**", "/api/fees/**",
                        "/api/inventory/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/students/**", "/api/fees/**",
                        "/api/inventory/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                // Attendance & timetable writes: admin + faculty.
                .requestMatchers(HttpMethod.POST, "/api/attendance/**",
                        "/api/timetable/**").hasAnyRole("ADMIN", "FACULTY")
                .requestMatchers(HttpMethod.PUT, "/api/attendance/**",
                        "/api/timetable/**").hasAnyRole("ADMIN", "FACULTY")
                // Everything else under /api: any logged-in role (read access).
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // lets the H2 console render
            .httpBasic(withDefaults());
        return http.build();
    }
}
