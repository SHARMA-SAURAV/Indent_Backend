package com.example.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class IndentRequestDTO {
    private Long id;
    private String itemName;
    private int quantity;
    private Long perPieceCost;
    private String description;
    private String flaUsername;  // Username of the FLA
    private String remarkBySla;  // Remark added by SLA
    private String status;

    // Constructor
    public IndentRequestDTO(Long id, String itemName, int quantity, Long perPieceCost, String description,
                            String flaUsername, String remarkBySla, String status) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.perPieceCost = perPieceCost;
        this.description = description;
        this.flaUsername = flaUsername;
        this.remarkBySla = remarkBySla;
        this.status = status;
    }

//    // Getters and setters
//    public Long getId() { return id; }
//    public String getItemName() { return itemName; }
//    public int getQuantity() { return quantity; }
//    public Long getPerPieceCost() { return perPieceCost; }
//    public String getDescription() { return description; }
//    public String getFlaUsername() { return flaUsername; }
//    public String getRemarkBySla() { return remarkBySla; }
//    public String getStatus() { return status; }
}

