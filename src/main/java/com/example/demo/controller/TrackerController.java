package com.example.demo.controller;

//package com.example.demo.controller;

import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.PurchaseReview;
import com.example.demo.model.User;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/track")
public class TrackerController {
    @Autowired
    private IndentRequestRepository indentRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("fla/all")
    public ResponseEntity<?> getAllIndentsForFLA(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");

        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User flauser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("FLA user not found"));
        List<IndentRequest> assignedIndents = indentRequestRepository.findByFla(flauser);
        return ResponseEntity.ok(assignedIndents);
    }

    @GetMapping("sla/all")
    public ResponseEntity<?> getAllIndentsForSLA(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");

        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User slauser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("SLA user not found"));
        List<IndentRequest> assignedIndents = indentRequestRepository.findBySla(slauser);
        return ResponseEntity.ok(assignedIndents);
    }


    @GetMapping("store/all")
    public ResponseEntity<?> getAllIndentsForStore(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");

        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User storeuser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Store user not found"));
        List<IndentRequest> assignedIndents = indentRequestRepository.findByStore(storeuser);
        return ResponseEntity.ok(assignedIndents);
    }


    @GetMapping("/finance/tracking")
    @PreAuthorize("hasRole('FINANCE')")
    public ResponseEntity<?> getIndentsForFinanceTracking(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        // Optionally filter by status or finance assignment if needed
        List<IndentRequest> all = indentRequestRepository.findAllWithUser();  // Includes user, fla, sla, etc.

        List<IndentRequest> financeRelevant = all.stream()
                .filter(indent ->
                        indent.getStatus().ordinal() >= IndentStatus.PENDING_FINANCE.ordinal() // Only those in/after finance stage
                )
                .toList();

        return ResponseEntity.ok(financeRelevant);
    }








}