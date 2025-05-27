package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewer;
    private String comment;
    private LocalDateTime reviewedAt;
    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name = "indent_request_id")
    private IndentRequest indentRequest;

    public PurchaseReview(String reviewer, String comment, IndentRequest indentRequest) {
        this.reviewer = reviewer;
        this.comment = comment;
        this.reviewedAt = LocalDateTime.now();
        this.reviewDate = LocalDateTime.now();
        this.indentRequest = indentRequest;
    }
}
