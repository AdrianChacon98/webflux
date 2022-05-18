package com.example.webflux.filter;

import com.example.webflux.repository.UsersReposiroty;
import com.example.webflux.security.AuthenticationManagerJwt;

import com.example.webflux.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Component
public class AuthorizationFilter implements WebFilter {

    @Autowired
    private AuthenticationManagerJwt authenticationManagerJwt;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersReposiroty usersReposiroty;





    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //get Request and response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //get path
        String path = request.getPath().contextPath().value();



        if(path.equals("/api/v1/users/create") || path.equals("/api/v1/users/login")){


            Authentication auth = (Authentication) authenticationManagerJwt.authenticate(new UsernamePasswordAuthenticationToken(null,null,null));
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }

        //get token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(token!=null){

            //get jwt
            String jwt = token.substring(7);

            String email = jwtUtil.getSubject(jwt);

            UserDetails user = (UserDetails) usersReposiroty.findByEmail(email);

            if(jwtUtil.validate(jwt,user)){





            }else{
                throw new UnsupportedJwtException("JWT expired");
                //throw new UnsupportedJwtException();
            }

        }else{

            throw new UnsupportedJwtException("JWT is empty");


        }


        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader->authHeader.startsWith("Bearer "))
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .map(authHeader->{
                    String jwt = authHeader.substring(7);
                    return jwt;
                })
                .flatMap(jwt->authenticationManagerJwt.authenticate(new UsernamePasswordAuthenticationToken(null,jwt)))
                .flatMap(authentication -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
    }
}
