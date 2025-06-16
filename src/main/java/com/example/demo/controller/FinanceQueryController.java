package com.example.demo.controller;


import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.User;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class FinanceQueryController {
    @Autowired
    private IndentRequestRepository indentRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/finance/send-back/{indentId}")
    public ResponseEntity<?> sendBackToRole(
            @PathVariable Long indentId,
            @RequestParam String targetRole,
            @RequestParam String remarks,
            Authentication auth
    ) {

        System.err.println("asdfasfhjhkwei2938234rjkhf");

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        switch (targetRole.toUpperCase()) {
            case "FLA":
                indent.setStatus(IndentStatus.SENT_BACK_TO_FLA);
                indent.setFinanceRemarks(remarks);
                indent.setFinanceReamrksDate(LocalDateTime.now());
                break;
            case "SLA":
                indent.setStatus(IndentStatus.SENT_BACK_TO_SLA);
                indent.setFinanceRemarks(remarks);
                indent.setFinanceReamrksDate(LocalDateTime.now());
                break;
            case "STORE":
                indent.setStatus(IndentStatus.SENT_BACK_TO_STORE);
                indent.setFinanceRemarks(remarks);
                indent.setFinanceReamrksDate(LocalDateTime.now());
                break;
            case "PURCHASE":
                indent.setStatus(IndentStatus.SENT_BACK_TO_PURCHASE);
                indent.setFinanceRemarks(remarks);
                indent.setFinanceReamrksDate(LocalDateTime.now());
                break;
            default:
                throw new RuntimeException("Invalid role");
        }

        indentRequestRepository.save(indent);
        return ResponseEntity.ok(Map.of(
                "message", "Indent sent back to " + targetRole + " successfully."
        ));
    }

    @PutMapping("/resubmit/{indentId}")
    public ResponseEntity<?> resubmitToFinance(
            @PathVariable Long indentId,
            @RequestParam String remarks,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            Authentication auth
    ) throws IOException {
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        indent.setStatus(IndentStatus.RESUBMITTED_TO_FINANCE);
        indent.setAgainUpdatedAt(LocalDateTime.now());
        indent.setProductRemarks(remarks); // or role-specific remark field

        if (attachment != null && !attachment.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + java.io.File.separator + "uploads";
            String fileName = System.currentTimeMillis() + "_" + attachment.getOriginalFilename();
            java.io.File uploadPath = new java.io.File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            java.io.File dest = new java.io.File(uploadPath, fileName);
            attachment.transferTo(dest);
            indent.setFileName(fileName);
            indent.setFileUrl("/uploads/" + fileName);
            indent.setFileType(attachment.getContentType());
            indent.setFileSize(attachment.getSize());
        }

        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of(
                "message", "Indent resubmitted to Finance successfully."
        ));
    }

    @GetMapping("/returned-to-role")
    public ResponseEntity<?> getReturnedIndentsForRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        System.err.println("heu heiehehueheuheuheuheuheuheheuheuh");

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String role = currentUser.getRoles().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .name().toUpperCase();

        List<IndentStatus> targetStatuses = switch (role) {
            case "FLA" -> List.of(IndentStatus.SENT_BACK_TO_FLA);
            case "SLA" -> List.of(IndentStatus.SENT_BACK_TO_SLA);
            case "STORE" -> List.of(IndentStatus.SENT_BACK_TO_STORE);
            case "PURCHASE" -> List.of(IndentStatus.SENT_BACK_TO_PURCHASE);
            case "USER" -> List.of(); // No yield needed
            default -> throw new RuntimeException("Invalid role: " + role);
        };

        if (targetStatuses.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<IndentRequest> returnedIndents = indentRequestRepository
                .findByStatusInAndRelevantRole(targetStatuses, (long) currentUser.getId());

        return ResponseEntity.ok(returnedIndents);
    }





}
