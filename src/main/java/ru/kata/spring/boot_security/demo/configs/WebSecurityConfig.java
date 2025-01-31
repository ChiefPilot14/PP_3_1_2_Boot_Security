package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder createBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect getSpringSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public SecurityFilterChain configureFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                        auth -> auth.requestMatchers("/", "/login", "/registration").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/user").hasAnyRole("USER", "ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login"))

                .formLogin(login -> login.successHandler((request,
                                                          response, authentication) -> {
                    String role = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("No roles found"));

                    if ("ROLE_ADMIN".equals(role)) {
                        response.sendRedirect("/admin");
                    } else if ("ROLE_USER".equals(role)) {
                        response.sendRedirect("/user");
                    }
                }));

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true));

        return http.build();
    }
//    @Bean
//    public FilterRegistrationBean openEntityManagerInViewFilter() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        OpenEntityManagerInViewFilter filter = new OpenEntityManagerInViewFilter();
//        registrationBean.setFilter(filter);
//        return registrationBean;
//    }
}