package com.ncinga.chatservice.MongoModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data // Generates getters, setters, toString(), equals(), and hashcode automatically
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder // Allows you to build objects using the builder pattern
@Document(collection = "users") // Marks this class as a MongoDB document
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String userRole;
    private String company;

    // Optional: Custom toString() method if you want more control over the output
    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', email='" + email + "', userRole='" + userRole + "', company='" + company + "'}";
    }
}
