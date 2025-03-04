package com.ncinga.chatservice.repository;

import com.ncinga.chatservice.document.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

//    Optional<Token> findByToken(String token);

    Optional<Token> findByJti(String jti);

}
