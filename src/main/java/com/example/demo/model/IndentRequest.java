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
    private IndentStatus status = IndentStatus.PENDING_FLA; // Default status

    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy; // User who created the indent

    @ManyToOne
    @JoinColumn(name = "fla_id")
    private User fla; // Assigned FLA

    @ManyToOne
    @JoinColumn(name = "sla_id")
    private User sla; // Assigned SLA

    @OneToMany(mappedBy = "indentRequest", cascade = CascadeType.ALL)
    private List<IndentRemark> remarks;

    private Date createdAt = new Date();
    private Date updatedAt ; // To track the last update time
}
