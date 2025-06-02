package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int quantity;
    private long perPieceCost;
    private double totalCost;
    private String description;
    private String purpose;
    private String department;
    private String specificationModelDetails;
    private String category; // Recurring or Capital

    private String attachmentPath; // File name or path

    @ManyToOne
    @JoinColumn(name = "indent_request_id")
    private IndentRequest indentRequest;
}
