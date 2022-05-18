package com.example.webflux.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig  {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){

        http

                .authorizeExchange(exchanges->exchanges
                        .pathMatchers(HttpMethod.POST,"/api/v1/user/**").permitAll()
                        .anyExchange().authenticated()
                        .and()
                        .httpBasic()
                            .disable()
                        .csrf()
                            .disable()
                        .formLogin()
                            .disable()
                        .logout()
                            .disable()
                        .addFilterAt(, SecurityWebFiltersOrder.AUTHENTICATION)

                );
        return http.build();
    }




}
