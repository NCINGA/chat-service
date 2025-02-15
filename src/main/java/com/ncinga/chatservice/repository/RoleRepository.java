package com.ncinga.chatservice.repository;

import com.ncinga.chatservice.document.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

    Role findByRole(String roleName);

}
