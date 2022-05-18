package com.example.webflux.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class LoginFailedHandler implements ServerAuthenticationFailureHandler {


    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        /*
        MyResult result = new MyResult("Authentication failure"); //Object to be JSON
        HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
        httpMessageConverter.write(result, CONTENT_TYPE_JSON, outputMessage); //Write to Response
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized.
        */

        if(exception.getClass().isAssignableFrom(UsernameNotFoundException.class)){

            ServerResponse
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("Error")
                    .switchIfEmpty(ServerResponse.notFound().build());

        }



        return Mono.empty();

    }



}
