package com.ncinga.chatservice.Repo;

import com.ncinga.chatservice.MongoModel.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepo extends MongoRepository<User, String> {
    List<User> findAll();

    User save(User user);
}
