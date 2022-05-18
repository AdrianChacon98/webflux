package com.example.webflux.controller;

import com.example.webflux.model.MessageResponse;
import com.example.webflux.model.Users;
import com.example.webflux.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Autowired
    private UsersService usersService;



    @PostMapping("/create")
    public Mono<ServerResponse> register(@RequestBody Users user){

        return usersService.register(user)
                .flatMap(message->{
                    if(message.equals("User was created successfully")){

                        return ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new MessageResponse(message))
                                .switchIfEmpty(ServerResponse.notFound().build());

                    }else{

                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new MessageResponse(message))
                                .switchIfEmpty(ServerResponse.notFound().build());
                    }


                }).onErrorResume(error-> {

                    WebClientResponseException errorResponse = (WebClientResponseException) error;

                    if(errorResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {

                        return ServerResponse.status(500)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new MessageResponse("Internal Server Error"));
                    }

                    return ServerResponse.notFound().build();
                });
    }


    @PostMapping("/login")
    public Mono<ServerResponse> login(@RequestParam("email") String email, @RequestParam("password") String password){

        return usersService.login(email,password)
                .onErrorResume(error->{

                    WebClientResponseException errorResponse = (WebClientResponseException) error;

                    if(errorResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {

                        return ServerResponse.status(500)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new MessageResponse("Internal Server Error"));
                    }

                    return ServerResponse.notFound().build();

                });

    }






}
