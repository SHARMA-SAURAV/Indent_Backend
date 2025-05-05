//package controller;
//
////package com.villysiu.controller;
////
////import com.villysiu.model.IndentRequest;
////import com.villysiu.model.User;
////import com.villysiu.repository.IndentRequestRepository;
////import com.villysiu.repository.UserRepository;
////import com.villysiu.service.EmailService;
//import Model.IndentRequest;
//import Model.User;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//import repository.UserRepository;
//import service.EmailService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserController {
//
//    private final IndentRequestRepository indentRequestRepository;
//    private final UserRepository userRepository;
//    private final EmailService emailService;
//
//    public UserController(IndentRequestRepository indentRequestRepository, UserRepository userRepository, EmailService emailService) {
//        this.indentRequestRepository = indentRequestRepository;
//        this.userRepository = userRepository;
//        this.emailService = emailService;
//    }
//
//    @PostMapping("/submit-indent")
//    public ResponseEntity<String> submitIndentRequest(@RequestBody IndentRequest indentRequest) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        User user = userRepository.findByUsername(username).orElseThrow();
//
//        indentRequest.setUser(user);
//        indentRequest.setStatus("PENDING");
//        indentRequestRepository.save(indentRequest);
//
//        // Notify Admin
//        String adminEmail = "admin@example.com"; // Replace with actual admin email
//        String subject = "New Indent Request Submitted";
//        String body = "A new indent request has been submitted by " + user.getUsername() +
//                ".\n\nProject Name: " + indentRequest.getProjectName() +
//                "\nAmount Requested: " + indentRequest.getAmountRequested() +
//                "\nStatus: PENDING";
//
//        emailService.sendEmail(adminEmail, subject, body);
//
//        return ResponseEntity.ok("Indent request submitted successfully!");
//    }
//}
