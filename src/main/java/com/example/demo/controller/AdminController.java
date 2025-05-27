//package com.example.demo.controller;
//
//import com.example.demo.model.IndentRequest;
//import com.example.demo.model.RoleType;
//import com.example.demo.model.User;
//import com.example.demo.repository.IndentRequestRepository;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private EmailService emailService;
//    @Autowired
//    private IndentRequestRepository indentRequestRepository;
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/all-indents")
//    public ResponseEntity<?> getAllIndentsGroupedByUser() {
//        List<IndentRequest> allIndents = indentRequestRepository.findAllWithUser();
//
//        Map<String, List<Map<String, Object>>> userIndents = new HashMap<>();
//
//        for (IndentRequest indent : allIndents) {
//            String username = indent.getRequestedBy().getUsername();
//            Map<String, Object> indentData = new HashMap<>();
//            indentData.put("indentId", indent.getId());
//            indentData.put("projectName", indent.getProjectName());
//            indentData.put("itemName", indent.getItemName());
//            indentData.put("quantity", indent.getQuantity());
//            indentData.put("perPieceCost", indent.getPerPieceCost());
//            indentData.put("totalCost", indent.getTotalCost());
//            indentData.put("status", indent.getStatus());
//            indentData.put("department", indent.getDepartment());
//            indentData.put("description", indent.getDescription());
//            indentData.put("purpose", indent.getPurpose());
//            indentData.put("specificationModelDetails", indent.getSpecificationModelDetails());
//            // Add remarks from each role
//            indentData.put("remarkByFla", indent.getRemarkByFla());
//            indentData.put("remarkBySla", indent.getRemarkBySla());
//            indentData.put("remarkByStore", indent.getRemarkByStore());
//            indentData.put("remarkByFinance", indent.getRemarkByFinance());
//            indentData.put("remarkByPurchase", indent.getRemarkByPurchase());
//
//            // Add this indent under the username
//            userIndents.computeIfAbsent(username, k -> new java.util.ArrayList<>()).add(indentData);
//        }
//
//        return ResponseEntity.ok(userIndents);
//    }
//
//
//    //Update user role with email verification
//    @PutMapping("/users/{id}")
//    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Object> request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
////        user.setRoles((Set<RoleType>) request.get("role"));
//
//        String oldRole = user.getRoles().toString();
//        Set<RoleType> newRole = (Set<RoleType>) request.get("role");
//        user.setRoles(newRole);
//
//        userRepository.save(user);
//
//        // Send email notification
//        emailService.sendEmail(user.getEmail(), "Role Updated",
//                "Hello " + user.getUsername() + ",\n\nYour role has been changed from " + oldRole + " to " + newRole + ".");
//        return ResponseEntity.ok("User role updated and email sent");
//    }
//
//
//    // Delete user
//    @DeleteMapping("/users/{id}")
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        userRepository.deleteById(id);
//        return ResponseEntity.ok("User deleted successfully");
//    }
//}









package com.example.demo.controller;

import com.example.demo.model.IndentRequest;
import com.example.demo.model.RoleType;
import com.example.demo.model.User;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IndentRequestRepository indentRequestRepository;

    // ✅ Get all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // ✅ Create a new user with encoded password
//    @PostMapping("/create-user")
//    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
//        String username = (String) request.get("username");
//        String email = (String) request.get("email");
//        String rawPassword = (String) request.get("password");
//
//        @SuppressWarnings("unchecked")
//        List<String> roles = (List<String>) request.get("roles");
//
//        Set<RoleType> roleSet = roles.stream()
//                .map(RoleType::valueOf)
//                .collect(Collectors.toSet());
//
//        User user = new User();
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(rawPassword));
//        user.setRoles(roleSet);
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok("User created successfully");
//    }

    // ✅ Update user roles with email notification
//    @PutMapping("/users/{id}")
//    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Object> request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String oldRoles = user.getRoles().toString();
//
//        @SuppressWarnings("unchecked")
//        List<String> roleList = (List<String>) request.get("roles");
//
//        Set<RoleType> newRoles = roleList.stream()
//                .map(RoleType::valueOf)
//                .collect(Collectors.toSet());
//
//        user.setRoles(newRoles);
//        userRepository.save(user);
//
//        emailService.sendEmail(user.getEmail(), "Role Updated",
//                "Hello " + user.getUsername() + ",\n\nYour role has been changed from " + oldRoles + " to " + newRoles + ".");
//
//        return ResponseEntity.ok("User roles updated and email sent.");
//    }

//    @DeleteMapping("/users/{id}")
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        // 1. Find the user first
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // 2. Handle all relationships in IndentRequest
//        List<IndentRequest> flaIndents = indentRequestRepository.findByFla(user);
//        flaIndents.forEach(indent -> indent.setFla(null));
//
//        List<IndentRequest> slaIndents = indentRequestRepository.findBySla(user);
//        slaIndents.forEach(indent -> indent.setSla(null));
//
//        List<IndentRequest> storeIndents = indentRequestRepository.findByStore(user);
//        storeIndents.forEach(indent -> indent.setStore(null));
//
//        List<IndentRequest> assignedIndents = indentRequestRepository.findByAssignedTo(user);
//        assignedIndents.forEach(indent -> indent.setAssignedTo(null));
//
//        // 3. Handle non-nullable requestedBy relationship
//        indentRequestRepository.detachRequestedBy(user);
//
//        // 4. Save all changes
//        List<IndentRequest> allIndents = new ArrayList<>();
//        allIndents.addAll(flaIndents);
//        allIndents.addAll(slaIndents);
//        allIndents.addAll(storeIndents);
//        allIndents.addAll(assignedIndents);
//
//        indentRequestRepository.saveAll(allIndents);
//
//        // 5. Now delete the user
//        userRepository.delete(user);
//
//        return ResponseEntity.ok("User deleted successfully");
//    }

    // ✅ View all indents grouped by user (with remarks)
    @GetMapping("/all-indents-with-remarks")
    public ResponseEntity<?> getAllIndentsGroupedByUser() {
        List<IndentRequest> allIndents = indentRequestRepository.findAllWithUser();

        Map<String, List<Map<String, Object>>> userIndents = new HashMap<>();

        for (IndentRequest indent : allIndents) {
            String username = indent.getRequestedBy().getUsername();
            Map<String, Object> indentData = new HashMap<>();
            indentData.put("indentId", indent.getId());
            indentData.put("projectName", indent.getProjectName());
            indentData.put("itemName", indent.getItemName());
            indentData.put("quantity", indent.getQuantity());
            indentData.put("perPieceCost", indent.getPerPieceCost());
            indentData.put("totalCost", indent.getTotalCost());
            indentData.put("status", indent.getStatus());
            indentData.put("department", indent.getDepartment());
            indentData.put("description", indent.getDescription());
            indentData.put("purpose", indent.getPurpose());
            indentData.put("specificationModelDetails", indent.getSpecificationModelDetails());
            indentData.put("remarkByFla", indent.getRemarkByFla());
            indentData.put("remarkBySla", indent.getRemarkBySla());
            indentData.put("remarkByStore", indent.getRemarkByStore());
            indentData.put("remarkByFinance", indent.getRemarkByFinance());
            indentData.put("remarkByPurchase", indent.getRemarkByPurchase());
            userIndents.computeIfAbsent(username, k -> new ArrayList<>()).add(indentData);
        }

        return ResponseEntity.ok(userIndents);
    }
}
