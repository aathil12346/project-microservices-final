package com.bankingapp.account_services.config;

import com.bankingapp.account_services.security.JwtAuthenticationEntryPoint;
import com.bankingapp.account_services.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private JwtAuthenticationFilter authenticationFilter;
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)-> authorize.requestMatchers(HttpMethod.POST
                ,"v1/account-services/create-account").permitAll()
                        .requestMatchers(HttpMethod.GET,"v1/account-services/verify").permitAll()
                        .requestMatchers(HttpMethod.POST,"v1/account-services/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"v1/account-services/account-exists/{accountNumber}").permitAll()
                        .requestMatchers(HttpMethod.POST,"v1/account-services/debit-money").permitAll()
                        .requestMatchers(HttpMethod.POST,"v1/account-services/credit-money").permitAll()
                        .anyRequest().authenticated())

                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint) )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
