package com.ncinga.chatservice.repository;

import com.ncinga.chatservice.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("{ 'email': ?0, 'role': ?1 }")
    Optional<User> getUserByEmailAndPasswordAndRole(
            @Param("email") String email,
            @Param("role") String role
    );

}
