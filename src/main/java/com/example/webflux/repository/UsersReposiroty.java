package com.example.webflux.repository;

import com.example.webflux.model.Users;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Repository
public interface UsersReposiroty extends ReactiveMongoRepository<Users,String> {




    public Mono<Optional<Users>> findByEmail(String email);




}
