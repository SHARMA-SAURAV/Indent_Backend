package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "indent_products")
@Getter
@Setter
public class IndentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATIONSHIP =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indent_request_id", nullable = false)
    @JsonBackReference("indent-products")
    private IndentRequest indentRequest;

    // ===== PRODUCT DETAILS =====
    @Column(nullable = false)
    private String itemName;

    private String department;
    private String category;
private String purpose;
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long perPieceCost;

    @Column(nullable = false)
    private Double totalCost;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "specification_model_details", columnDefinition = "TEXT")
    private String specificationModelDetails;
private String specification;
    // ===== FILE ATTACHMENTS =====
    private String fileName;
    private String fileUrl;
    private String attachmentPath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime fileUploadedAt;

    // ===== PRODUCT STATUS =====
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ProductStatus productStatus = ProductStatus.PENDING;
//private String flaRemarks;
    // ===== PRODUCT-LEVEL REMARKS =====
    @Column(columnDefinition = "TEXT")
    private String productRemarks;

    @Column(columnDefinition = "TEXT")
    private String flaRemarks;
    private LocalDateTime flaRemarksDate;
    @Column(columnDefinition = "TEXT")
    private String slaRemarks;
    private LocalDateTime slaRemarksDate;
    @Column(columnDefinition = "TEXT")
    private String storeRemarks;
    private  LocalDateTime storeRemarksDate;
    @Column(columnDefinition = "TEXT")
    private String financeRemarks;
    private LocalDateTime financeReamrksDate;
    @Column(columnDefinition = "TEXT")
    private String purchaseRemarks;
    private LocalDateTime purchaseRemarkDate;
    // ===== AUDIT FIELDS =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    // ===== MODIFICATION TRACKING =====
    private Integer originalQuantity;
    private Long originalPerPieceCost;
    private Double originalTotalCost;
    private LocalDateTime modifiedAt;
    private String modifiedBy; // Role who made the modification

    // ===== PRODUCT SEQUENCE =====
    private Integer productSequence; // Order of products within the indent

    // ===== UTILITY METHODS =====

    /**
     * Calculate total cost based on quantity and per piece cost
     */
    public void calculateTotalCost() {
        if (quantity != null && perPieceCost != null) {
            this.totalCost = quantity * perPieceCost.doubleValue();
        }
    }

    /**
     * Store original values for modification tracking
     */
    public void storeOriginalValues() {
        this.originalQuantity = this.quantity;
        this.originalPerPieceCost = this.perPieceCost;
        this.originalTotalCost = this.totalCost;
    }

    /**
     * Check if this product has been modified
     */
    public boolean isModified() {
        return !quantity.equals(originalQuantity) ||
                !perPieceCost.equals(originalPerPieceCost);
    }

    /**
     * Apply modifications to the product
     */
    public void applyModification(Integer newQuantity, Long newPerPieceCost, String modifierRole, String remarks) {
        // Store original values if not already stored
        if (originalQuantity == null) {
            storeOriginalValues();
        }

        if (newQuantity != null) {
            this.quantity = newQuantity;
        }

        if (newPerPieceCost != null) {
            this.perPieceCost = newPerPieceCost;
        }

        // Recalculate total cost
        calculateTotalCost();

        // Update modification tracking
        this.modifiedAt = LocalDateTime.now();
        this.modifiedBy = modifierRole;
        this.productStatus = ProductStatus.MODIFIED;
        this.productRemarks = remarks;
    }

    /**
     * Approve this product
     */
    public void approve(String approverRole, String remarks) {
        this.productStatus = ProductStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Set role-specific remarks
        setRemarksByRole(approverRole, remarks);
    }

    /**
     * Reject this product
     */
    public void reject(String rejectorRole, String remarks) {
        this.productStatus = ProductStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Set role-specific remarks
        setRemarksByRole(rejectorRole, remarks);
    }

    /**
     * Set remarks based on role
     */
    private void setRemarksByRole(String role, String remarks) {
        switch (role.toUpperCase()) {
            case "FLA":
                this.flaRemarks = remarks;
                break;
            case "SLA":
                this.slaRemarks = remarks;
                break;
            case "STORE":
                this.storeRemarks = remarks;
                break;
            case "FINANCE":
                this.financeRemarks = remarks;
                break;
            case "PURCHASE":
                this.purchaseRemarks = remarks;
                break;
            default:
                this.productRemarks = remarks;
        }
    }

    /**
     * Get formatted cost display
     */
    public String getFormattedTotalCost() {
        return String.format("₹%.2f", totalCost);
    }

    /**
     * Get product summary for display
     */
    public String getProductSummary() {
        return String.format("%s - Qty: %d - %s",
                itemName, quantity, getFormattedTotalCost());
    }

    /**
     * Check if product has file attachment
     */
    public boolean hasFileAttachment() {
        return fileName != null && !fileName.trim().isEmpty();
    }

    /**
     * Get status display color for UI
     */
    public String getStatusColor() {
        if (productStatus == null) return "gray";

        switch (productStatus) {
            case PENDING:
                return "yellow";
            case APPROVED:
                return "green";
            case REJECTED:
                return "red";
            case MODIFIED:
                return "blue";
            default:
                return "gray";
        }
    }

    // ===== LIFECYCLE METHODS =====

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }

        // Calculate total cost if not set
        if (totalCost == null || totalCost == 0) {
            calculateTotalCost();
        }

        // Store original values for tracking
        if (originalQuantity == null) {
            storeOriginalValues();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();

        // Recalculate total cost
        calculateTotalCost();
    }

    // ===== EQUALS AND HASHCODE =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndentProduct)) return false;
        IndentProduct that = (IndentProduct) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IndentProduct{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", totalCost=" + totalCost +
                ", productStatus=" + productStatus +
                '}';
    }
}

// ===== PRODUCT STATUS ENUM =====

