package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
//@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;

    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters and setters
}
