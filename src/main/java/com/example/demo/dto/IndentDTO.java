package com.example.demo.dto;

import com.example.demo.model.IndentRequest;

// IndentDTO.java
public class IndentDTO {
    private Long id;
    private String itemName;
    private String status;
    private String requestedBy;

    // Constructor
    public IndentDTO(IndentRequest indent) {
        this.id = indent.getId();
        this.itemName = indent.getItemName();
        this.status = indent.getStatus().name();
        this.requestedBy = indent.getRequestedBy().getUsername();
    }

    // Getters
}
