//
//package com.example.demo.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//public class IndentRequest {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    // ===== INDENT LEVEL FIELDS =====
//    @Column(unique = true)
//    private String indentNumber; // Unique identifier for the entire indent
//    private String projectName;
//    private String purpose;
//    private String department;
//    //Total cost of all products in this indent
//    private double totalIndentCost;
//
//    private String itemName;
//    private String category;
//    private int quantity;
//    private String description;
//    private Long perPieceCost;
//    private double totalCost;
////    private String purpose;
//
//    @Column(name = "specification_model_details", columnDefinition = "TEXT")
//    private String specificationModelDetails;
//    private String fileName;
//    private String fileUrl;
//    private String fileType;
//    private Long fileSize;
//    @Enumerated(EnumType.STRING)
//    @Column(length = 50)
//    private IndentStatus status = IndentStatus.PENDING_FLA; // Default status
//    @ManyToOne
//    @JoinColumn(name = "requested_by_id", nullable = false)
//    private User requestedBy; // User who created the indent
//    private String remarkByFinance;
////    private LocalDateTime financeApprovalDate;
//    private String remarkByPurchase;
////    private LocalDateTime purchaseCompletionDate;
////    private LocalDateTime gfrGeneratedDate;
//    private String financeRemark;
////    private LocalDateTime paymentCompletedDate;
////    private LocalDateTime userInspectionDate;
//    private String gfrNote;
//    private String paymentNote;
////    private LocalDateTime gfrCreatedAt;
////    private LocalDateTime paymentCreatedAt;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Remark> remarks = new ArrayList<>();
//    private String remarkByUser;
//    private String userInspectionRemark;
//    private String gfrDetails;
////    private LocalDateTime paymentDate;
//    private String paymentRemark;
//    @ManyToOne
//    @JoinColumn(name = "fla_id")
//    private User fla; // Assigned FLA
//    @ManyToOne
//    @JoinColumn(name = "sla_id")
//    private User sla; // Assigned SLA
//    public void addRemark(String role, String message) {
//        this.remarks.add(new Remark(role, message, this));
//    }
//
//
//
//    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference("indent-products")
//    private List<IndentItem> items = new ArrayList<>();
//
//    // ===== MULTI-PRODUCT SUPPORT =====
//    // Multiple products in this indent (NEW)
//    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JsonManagedReference("indent-products")
//    private List<IndentProduct> products = new ArrayList<>();
//
//    private String remarkByStore;
////    private LocalDateTime storeApprovalDate;
//    @ManyToOne
//    @JoinColumn(name = "store_id")
//    private User store;
//    @ManyToOne
//    @JoinColumn(name = "finance_id")
//    private User finance; // Finance user who approves the payment
//    @ManyToOne
//    @JoinColumn(name = "purchase_id")
//    private User purchase;
//
//    @ManyToOne
//    @JoinColumn(name = "assigned_to_id")
//    private User assignedTo; // Currently responsible user (e.g., store/finance/purchase)
////    private LocalDateTime createdAt ;
////    private LocalDateTime flaApprovalDate;
//    private String remarkByFla;
//    private String remarkBySla;
////    private LocalDateTime slaApprovalDate;
//    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
//    @JsonIgnore
//    @JsonManagedReference
//    private List<PurchaseReview> reviews = new ArrayList<>();
//    // ===== APPROVAL TRACKING =====
//    private LocalDateTime createdAt;
//    private LocalDateTime flaApprovalDate;
//    private LocalDateTime slaApprovalDate;
//    private LocalDateTime storeApprovalDate;
//    private LocalDateTime financeApprovalDate;
//    private LocalDateTime purchaseCompletionDate;
//    private LocalDateTime gfrGeneratedDate;
//    private LocalDateTime paymentCompletedDate;
//    private LocalDateTime userInspectionDate;
//    private LocalDateTime gfrCreatedAt;
//    private LocalDateTime paymentCreatedAt;
//    private LocalDateTime paymentDate;
//
//
//    // ===== REMARKS BY DIFFERENT ROLES =====
//    private String remarkByFla;
//    private String remarkBySla;
//    private String remarkByStore;
//    private String remarkByFinance;
//    private String remarkByPurchase;
//    private String remarkByUser;
//    private String userInspectionRemark;
//    private String financeRemark;
//    private String gfrNote;
//    private String paymentNote;
//    private String gfrDetails;
//    private String paymentRemark;
//
//
//    public void addPurchaseReview(String reviewer, String comment) {
//        this.reviews.add(new PurchaseReview(reviewer, comment, this));
//    }
//
//    private boolean inwardEntryGenerated;
////    inwardEntryGenerated= false; // Flag to indicate if inward entry is generated
//    private Date updatedAt ; // To track the last update time
//
//    // Additional metadata for file handling
//    private LocalDateTime fileUploadedAt;
//    private String uploadedByRole;
//
//    // Batch processing fields
//    private String batchId; // To group multiple indents created together
//    private Integer batchSequence; // Order within the batch
//
//}









package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Data
public class IndentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== INDENT LEVEL FIELDS =====
    @Column(unique = true)
    private String indentNumber; // Unique identifier for the entire indent

    private String projectName;
    private String purpose;
    private String department;

    // Total cost of all products in this indent
    private Double totalIndentCost;

    // Keep existing fields for backward compatibility (single product indents)
    private String itemName; // For single product indents
    private String category; // For single product indents
    private int quantity; // For single product indents
    private String description; // For single product indents
    private Long perPieceCost; // For single product indents
    private double totalCost; // For single product indents - will be same as totalIndentCost for single items

    @Column(name = "specification_model_details", columnDefinition = "TEXT")
    private String specificationModelDetails; // For single product indents

    // File fields for single product indents
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private IndentStatus status = IndentStatus.PENDING_FLA;

    // ===== USER ASSIGNMENTS =====
    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "fla_id")
    private User fla;

    @ManyToOne
    @JoinColumn(name = "sla_id")
    private User sla;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private User store;

    @ManyToOne
    @JoinColumn(name = "finance_id")
    private User finance;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private User purchase;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    // ===== MULTI-PRODUCT SUPPORT =====
    // Multiple products in this indent (NEW)
    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("indent-products")
    private List<IndentProduct> products = new ArrayList<>();

    // Keep existing IndentItem relationship for backward compatibility
    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference("indent-items")
    private List<IndentProduct> items = new ArrayList<>();

    // ===== APPROVAL TRACKING =====
    private LocalDateTime createdAt;
    private LocalDateTime flaApprovalDate;
    private LocalDateTime slaApprovalDate;
    private LocalDateTime storeApprovalDate;
    private LocalDateTime financeApprovalDate;
    private LocalDateTime purchaseCompletionDate;
    private LocalDateTime gfrGeneratedDate;
    private LocalDateTime paymentCompletedDate;
    private LocalDateTime userInspectionDate;
    private LocalDateTime gfrCreatedAt;
    private LocalDateTime paymentCreatedAt;
    private LocalDateTime paymentDate;

    // ===== REMARKS BY DIFFERENT ROLES =====
    private String remarkByFla;
    private String remarkBySla;
    private String remarkByStore;
    private String remarkByFinance;
    private String remarkByPurchase;
    private String remarkByUser;
    private String userInspectionRemark;
    private String financeRemark;
    private String gfrNote;
    private String paymentNote;
    private String gfrDetails;
    private String paymentRemark;

    // ===== EXISTING RELATIONSHIPS =====
    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
    @JsonManagedReference("indent-remarks")
    private List<Remark> remarks = new ArrayList<>();

    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
    @JsonIgnore
    @JsonManagedReference("indent-reviews")
    private List<PurchaseReview> reviews = new ArrayList<>();

    // ===== UTILITY FIELDS =====
    private boolean inwardEntryGenerated = false;
    private Date updatedAt;

    // File handling metadata
    private LocalDateTime fileUploadedAt;
    private String uploadedByRole;

    // Batch processing fields
    private String batchId;
    private Integer batchSequence;

    //attachment path
    private String attachmentPath;
    // ===== UTILITY METHODS =====

    /**
     * Add a remark to this indent
     */
    public void addRemark(String role, String message) {
        this.remarks.add(new Remark(role, message, this));
    }

    /**
     * Add a purchase review to this indent
     */
    public void addPurchaseReview(String reviewer, String comment) {
        this.reviews.add(new PurchaseReview(reviewer, comment, this));
    }

    /**
     * Add a product to this indent
     */
    public void addProduct(IndentProduct product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        this.products.add(product);
        product.setIndentRequest(this);

        // Recalculate total cost
        recalculateTotalCost();
    }

    /**
     * Remove a product from this indent
     */
    public void removeProduct(IndentProduct product) {
        if (this.products != null) {
            this.products.remove(product);
            product.setIndentRequest(null);

            // Recalculate total cost
            recalculateTotalCost();
        }
    }

    /**
     * Check if this is a multi-product indent
     */
    public boolean isMultiProduct() {
        return products != null && !products.isEmpty();
    }

    /**
     * Get the count of products in this indent
     */
    public int getProductCount() {
        if (isMultiProduct()) {
            return products.size();
        }
        return itemName != null ? 1 : 0; // Single product or no product
    }

    /**
     * Recalculate total indent cost based on products
     */
    public void recalculateTotalCost() {
        if (isMultiProduct()) {
            this.totalIndentCost = products.stream()
                    .mapToDouble(IndentProduct::getTotalCost)
                    .sum();
        } else {
            // For single product indents, use the existing totalCost field
            this.totalIndentCost = this.totalCost;
        }
    }

    /**
     * Get display cost (total of all products or single product cost)
     */
    public Double getDisplayTotalCost() {
        if (totalIndentCost != null && totalIndentCost > 0) {
            return totalIndentCost;
        }
        return totalCost; // Fallback to single product cost
    }

    /**
     * Check if all products are approved
     */
    public boolean areAllProductsApproved() {
        if (!isMultiProduct()) {
            return true; // Single product indents don't have individual product status
        }

        return products.stream()
                .allMatch(product ->
                        product.getProductStatus() == ProductStatus.APPROVED ||
                                product.getProductStatus() == ProductStatus.MODIFIED);
    }

    /**
     * Check if any product is rejected
     */
    public boolean hasRejectedProducts() {
        if (!isMultiProduct()) {
            return false;
        }

        return products.stream()
                .anyMatch(product -> product.getProductStatus() == ProductStatus.REJECTED);
    }

    /**
     * Get approved products only
     */
    public List<IndentProduct> getApprovedProducts() {
        if (!isMultiProduct()) {
            return new ArrayList<>();
        }

        return products.stream()
                .filter(product ->
                        product.getProductStatus() == ProductStatus.APPROVED ||
                                product.getProductStatus() == ProductStatus.MODIFIED)
                .toList();
    }

    /**
     * Generate indent summary for display
     */
    public String getIndentSummary() {
        if (isMultiProduct()) {
            return String.format("Indent %s - %d products (₹%.2f)",
                    indentNumber, getProductCount(), getDisplayTotalCost());
        } else {
            return String.format("Indent %s - %s (₹%.2f)",
                    indentNumber != null ? indentNumber : "N/A",
                    itemName != null ? itemName : "No item",
                    getDisplayTotalCost());
        }
    }

    // ===== LIFECYCLE METHODS =====

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = new Date();
        }
        if (indentNumber == null) {
            generateIndentNumber();
        }
        recalculateTotalCost();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        recalculateTotalCost();
    }

    /**
     * Generate unique indent number
     */
    private void generateIndentNumber() {
        // Format: IND-YYYY-MM-DD-HHMMSS-XXX
        LocalDateTime now = LocalDateTime.now();
        String timestamp = String.format("%d%02d%02d%02d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond());
        int random = (int)(Math.random() * 999);
        this.indentNumber = String.format("IND-%s-%03d", timestamp, random);
    }

    public void addItem(IndentProduct item) {
        items.add(item);
        item.setIndentRequest(this);
    }

}