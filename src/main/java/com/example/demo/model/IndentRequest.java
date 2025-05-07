//package com.example.demo.model;
//
////package com.indentmanagement.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
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
//
//    private String itemName;
//    private int quantity;
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    private IndentStatus status = IndentStatus.PENDING;  // Default status
//
//    @ManyToOne
//    @JoinColumn(name = "requested_by")
//    private User requestedBy;
//
//    @ManyToOne
//    @JoinColumn(name = "fla_id")
//    private User fla;
//
//    @ManyToOne
//    @JoinColumn(name = "sla_id")
//    private User sla;
//
//    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
//    private List<IndentRemark> remarks;
//
//    private Date createdAt = new Date();
//}
//


package com.example.demo.model;

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

    private String itemName;
    private int quantity;
    private String description;
    private Long perPieceCost; // Cost per piece
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private IndentStatus status = IndentStatus.PENDING_FLA; // Default status

    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy; // User who created the indent

    private String remarkByFinance;
    private LocalDateTime financeApprovalDate;
    @ManyToOne
    @JoinColumn(name = "fla_id")
    private User fla; // Assigned FLA

    @ManyToOne
    @JoinColumn(name = "sla_id")
    private User sla; // Assigned SLA

    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
    private List<Remark> remarks=new ArrayList<>();

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

    private Date updatedAt ; // To track the last update time
}
