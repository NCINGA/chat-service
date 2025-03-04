package com.ncinga.chatservice.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "tokens")
public class Token {

    @Id
    private String id;

    private String jti;
    private Date expireDate;
    private boolean revoke = false;

    public void revoke(){
        this.revoke=true;
    }

}
