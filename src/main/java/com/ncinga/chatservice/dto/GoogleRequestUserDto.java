package com.ncinga.chatservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@AllArgsConstructor
@Data
public class GoogleRequestUserDto {
    private String id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
}
