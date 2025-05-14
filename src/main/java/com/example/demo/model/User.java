package com.example.demo.model;

//import org.springframework.validation.annotation.Validated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String phone;
    private String department;
    private String designation;
    private String employeeId;
    private String sex;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RoleType> roles = new HashSet<>();


    public String getFullName() {
        return name;
    }

}
