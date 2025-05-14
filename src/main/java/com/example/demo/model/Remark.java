package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class Remark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role; // e.g., "FLA", "SLA", "STORE", etc.
    private String message;
    private LocalDateTime createdAt;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indent_id")
    @JsonBackReference
    private IndentRequest indentRequest;

    // Constructors
    public Remark() {}

    public Remark(String role, String message, IndentRequest indentRequest) {
        this.role = role;
        this.message = message;
        this.indentRequest = indentRequest;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
}

