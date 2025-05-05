//package controller;
//
////package com.villysiu.controller;
//
////import com.villysiu.model.IndentRequest;
////import com.villysiu.repository.IndentRequestRepository;
////import com.villysiu.service.EmailService;
//import Model.IndentRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import service.EmailService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin")
//public class AdminController {
//
//    private final IndentRequestRepository indentRequestRepository;
//    private final EmailService emailService;
//
//    public AdminController(IndentRequestRepository indentRequestRepository, EmailService emailService) {
//        this.indentRequestRepository = indentRequestRepository;
//        this.emailService = emailService;
//    }
//
//    @GetMapping("/all-requests")
//    public ResponseEntity<List<IndentRequest>> getAllIndentRequests() {
//        return ResponseEntity.ok(indentRequestRepository.findAll());
//    }
//
//    @PostMapping("/update-status/{id}")
//    public ResponseEntity<String> updateIndentStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String remark) {
//        IndentRequest indentRequest = indentRequestRepository.findById(id).orElseThrow();
//
//        indentRequest.setStatus(status);
//        indentRequest.setRemark(remark);
//        indentRequestRepository.save(indentRequest);
//
//        // Notify User
//        String userEmail = indentRequest.getUser().getEmail();
//        String subject = "Indent Request Status Updated";
//        String body = "Your indent request for project **" + indentRequest.getProjectName() + "** has been **" + status.toUpperCase() + "**." +
//                "\n\nAdmin Remarks: " + (remark != null ? remark : "None");
//
//        emailService.sendEmail(userEmail, subject, body);
//
//        return ResponseEntity.ok("Indent request updated successfully!");
//    }
//}
