package com.example.webflux.security;

import com.example.webflux.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationManagerJwt implements ReactiveAuthenticationManager {


    @Autowired
    private JwtUtil jwtUtil;


    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {

        if(authentication.getCredentials().toString()==null){
            return Mono.just(new UsernamePasswordAuthenticationToken(null,null,null));
        }


        return Mono.just(authentication.getCredentials().toString())
                .map(jwt->{

                    Claims claims = jwtUtil.getAllClaimsFromToken(jwt);
                    return claims;
                })
                .map(claims -> {

                    String username = claims.get("username",String.class);

                    List<String> roles = claims.get("authorities",List.class);

                    List<GrantedAuthority> authorities = roles
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    return  new UsernamePasswordAuthenticationToken(username,null,authorities);

                });

    }



}
