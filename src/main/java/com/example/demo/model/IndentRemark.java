//package com.example.demo.model;
//
////package com.indentmanagement.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Date;
//
//@Entity
//@Getter
//@Setter
//public class IndentRemark {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "indent_request_id")
//    private IndentRequest indentRequest;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    private String remark;
//    private Date createdAt = new Date();
//}
//


package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class IndentRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "indent_request_id")
    private IndentRequest indentRequest;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String remark;
    private Date createdAt = new Date();
}
