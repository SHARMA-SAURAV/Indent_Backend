package com.example.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    private String requestedByName;
    private String flaName;
    private String slaName;
    private String storeName;
    private String remarkByFinance;
    private LocalDateTime financeApprovalDate;
    private String remarkByPurchase;
    private LocalDateTime purchaseCompletionDate;

    private LocalDateTime gfrGeneratedDate;
    private String gfrNote;
    private String gfrDetails;

    private String financeRemark;
    private LocalDateTime paymentCompletedDate;
    private String paymentNote;
    private LocalDateTime paymentCreatedAt;

    private LocalDateTime userInspectionDate;
    private String userInspectionRemark;

    private String remarkByStore;
    private LocalDateTime storeApprovalDate;

    private double totalCost;
    private String purpose;
    private String department;
    private String specificationModelDetails;
    private String projectName;
    private LocalDateTime flaApprovalDate;
    private String remarkByFla;
    private LocalDateTime slaApprovalDate;
//    private String remarkBySla;

    private Date createdAt;
//    private String flaUsername; // <-- Add this
    private Date updatedAt;
    private String slaUsername;

    private List<RemarkDTO> remarks;
    // Constructor
    public IndentRequestDTO(Long id, String itemName, int quantity, Long perPieceCost, String description,
                            String flaUsername , String slaUsername, String remarkBySla, String status) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.perPieceCost = perPieceCost;
        this.description = description;
        // use conditional operator for chekcing the flausername is null or not
        this.slaUsername=slaUsername!=null? slaUsername: null;
        this.flaUsername = flaUsername!=null? flaUsername: null;
        this.remarkBySla = remarkBySla;
        this.status = status;
    }
}

