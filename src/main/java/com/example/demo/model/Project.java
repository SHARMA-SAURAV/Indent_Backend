package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Project {
    @Id @GeneratedValue
    private Long id;
    private String ProjectName;
    private String description;
    private Long totalBudget;
//    private Long moneyLeft;
//    private Long Capital;
//    private String consumable;
//    private String category;
//    private String overhead;
    private double capitalAmount;
    private double consumableAmount;
    private double categoryAmount;
    private double overheadAmount;
//
    private double capitalBalance;
    private double consumableBalance;
    private double categoryBalance;
    private double overheadBalance;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Assuming User is another entity in your application

}
