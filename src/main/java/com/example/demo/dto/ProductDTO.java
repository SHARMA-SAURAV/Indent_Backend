package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
@AllArgsConstructor

public class ProductDTO {


    private String itemName;
    private String category;
    private Integer quantity;
    private Long perPieceCost;
    private String description;
    private String specificationModelDetails;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;

    // Constructors, getters, and setters
    public ProductDTO() {}

    public ProductDTO(String itemName, String category, Integer quantity, Long perPieceCost) {
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.perPieceCost = perPieceCost;
    }

}
