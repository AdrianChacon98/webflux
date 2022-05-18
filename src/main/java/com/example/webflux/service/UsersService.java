package com.example.webflux.service;

import com.example.webflux.enums.Roles;
import com.example.webflux.model.Users;
import com.example.webflux.repository.UsersReposiroty;
import com.example.webflux.security.PasswordEncoder;
import com.example.webflux.util.JwtUtil;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;


@Service
public class UsersService implements ReactiveUserDetailsService {


    @Autowired
    private UsersReposiroty usersReposiroty;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        /*
        return usersReposiroty.findByEmail(username)
                //.filter(Objects::isNull)
                .flatMap(user -> {
                    UserDetails userDetails = (UserDetails) user.get();
                    return Mono.just(userDetails);
                }).switchIfEmpty(user->{
                    UserDetails userDetails=null;
                    return Mono.just(userDetails);
                })
                */
        return null;
    }


    public Mono<String> register(Users user){

        return usersReposiroty.findByEmail(user.getEmail())
                .flatMap(userBrought->{

                    if(userBrought.isEmpty()){


                        //encode password
                        String password= PasswordEncoder.generateHash(user.getPassword());
                        user.setPassword(password);


                        //add role
                        user.setRole(Roles.USER.name());

                        //create the user
                        usersReposiroty.save(user);

                        return Mono.just("User was created successfully");

                    }else{

                        return Mono.just("User already exist");
                    }


                }).onErrorResume(error->{

                    Exception exception = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error while was creating the user");

                    return Mono.error(exception);
                });

    }


    public Mono<ServerResponse> login(String email,String password){

        return usersReposiroty.findByEmail(email)
                .flatMap(userBrought->{

                    if(!userBrought.isEmpty()){


                        //check password that has match
                        if(PasswordEncoder.isPasswordEqual(userBrought.get().getPassword(),password)){

                            //generate the jwt
                            Map<String,Object> jwt = jwtUtil.generateJwt(userBrought.get());

                            return ServerResponse
                                    .status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(jwt)
                                    .switchIfEmpty(ServerResponse.notFound().build());

                        }else{

                            //otherwise will notice that the user is not correct

                            return ServerResponse
                                    .status(HttpStatus.BAD_REQUEST)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue("The user or password is not correct")
                                    .switchIfEmpty(ServerResponse.notFound().build());

                        }

                    }else{

                        return ServerResponse
                                .status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("The user does not exist")
                                .switchIfEmpty(ServerResponse.notFound().build());
                    }

                }).onErrorResume(error->{

                    return ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("It was an error while it was generating the jwt")
                            .switchIfEmpty(ServerResponse.notFound().build());
                });

    }




}
