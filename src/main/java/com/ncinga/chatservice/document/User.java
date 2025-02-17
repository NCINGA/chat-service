package com.ncinga.chatservice.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    @DBRef
    private List<Role>  roles;

    @DBRef
    List<Authority> authorityList;

}
