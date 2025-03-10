package com.ncinga.chatservice.repository;

import com.ncinga.chatservice.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    List<User> findAll();

}
