
package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class IndentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String itemName;
    private int quantity;
    private String description;
    private Long perPieceCost;
    private double totalCost;
    private String purpose;
    private String department;
    @Column(name = "specification_model_details", columnDefinition = "TEXT")
    private String specificationModelDetails;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private IndentStatus status = IndentStatus.PENDING_FLA; // Default status
    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy; // User who created the indent
    private String remarkByFinance;
    private LocalDateTime financeApprovalDate;
    private String remarkByPurchase;
    private LocalDateTime purchaseCompletionDate;
    private LocalDateTime gfrGeneratedDate;
    private String financeRemark;
    private LocalDateTime paymentCompletedDate;
    private LocalDateTime userInspectionDate;
    private String gfrNote;
    private String paymentNote;
    private LocalDateTime gfrCreatedAt;
    private LocalDateTime paymentCreatedAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Remark> remarks = new ArrayList<>();
    private String remarkByUser;
    private String userInspectionRemark;
    private String gfrDetails;
    private LocalDateTime paymentDate;
    private String paymentRemark;
    @ManyToOne
    @JoinColumn(name = "fla_id")
    private User fla; // Assigned FLA
    @ManyToOne
    @JoinColumn(name = "sla_id")
    private User sla; // Assigned SLA
    public void addRemark(String role, String message) {
        this.remarks.add(new Remark(role, message, this));
    }
    private String remarkByStore;
    private LocalDateTime storeApprovalDate;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private User store;
    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo; // Currently responsible user (e.g., store/finance/purchase)
    private Date createdAt = new Date();
    private LocalDateTime flaApprovalDate;
    private String remarkByFla;
    private String remarkBySla;
    private LocalDateTime slaApprovalDate;
    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
    @JsonIgnore
    @JsonManagedReference
    private List<PurchaseReview> reviews = new ArrayList<>();

    public void addPurchaseReview(String reviewer, String comment) {
        this.reviews.add(new PurchaseReview(reviewer, comment, this));
    }

    private boolean inwardEntryGenerated;
//    inwardEntryGenerated= false; // Flag to indicate if inward entry is generated
    private Date updatedAt ; // To track the last update time

}
