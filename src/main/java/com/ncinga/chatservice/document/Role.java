package com.ncinga.chatservice.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Role {

    @Id
    private String id;

    private String role;

    @DBRef
    List<Authority> authorityList;

}
