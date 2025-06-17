package com.example.demo.controller;


import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.RoleType;
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
                // Fix: assign purchase user only if not null and handle errors gracefully
                try {
                    User purchaseUser = userRepository.findByRole(RoleType.PURCHASE).stream().findFirst().orElse(null);
                    if (purchaseUser != null) {
                        indent.setPurchase(purchaseUser);
                    } else {
                        System.err.println("[WARN] No purchase user found to assign.");
                    }
                } catch (Exception e) {
                    System.err.println("[WARN] Could not set purchase user: " + e.getMessage());
                }
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

        // Find the highest priority role (not USER)
        String role = currentUser.getRoles().stream()
            .map(r -> r.name().toUpperCase())
            .filter(r -> !r.equals("USER"))
            .findFirst()
            .orElse(currentUser.getRoles().stream().findFirst().map(r -> r.name().toUpperCase()).orElse("USER"));

        System.err.println("[DEBUG] User roles: " + currentUser.getRoles());
        System.err.println("[DEBUG] Detected role: " + role);

        List<IndentRequest> returnedIndents = List.of();
        Long userId = (long) currentUser.getId();

        switch (role) {
            case "FLA" -> returnedIndents = indentRequestRepository.findByStatusInAndFla(List.of(IndentStatus.SENT_BACK_TO_FLA), userId);
            case "SLA" -> returnedIndents = indentRequestRepository.findByStatusInAndSla(List.of(IndentStatus.SENT_BACK_TO_SLA), userId);
            case "STORE" -> {
                returnedIndents = indentRequestRepository.findByStatusInAndStore(List.of(IndentStatus.SENT_BACK_TO_STORE), userId);
                System.err.println("[DEBUG] STORE userId: " + userId);
                for (IndentRequest ir : returnedIndents) {
                    System.err.println("[DEBUG] STORE Indent: id=" + ir.getId() + ", status=" + ir.getStatus() + ", store.id=" + (ir.getStore() != null ? ir.getStore().getId() : null));
                }
            }
            case "PURCHASE" -> {
                // Always include both: assigned to this user and unassigned (purchase=null)
                List<IndentRequest> assigned = indentRequestRepository.findByStatusInAndPurchase(List.of(IndentStatus.SENT_BACK_TO_PURCHASE), userId);
                List<IndentRequest> unassigned = indentRequestRepository.findByStatusIn(List.of(IndentStatus.SENT_BACK_TO_PURCHASE))
                    .stream().filter(ir -> ir.getPurchase() == null).toList();
                returnedIndents = new java.util.ArrayList<>();
                returnedIndents.addAll(assigned);
                returnedIndents.addAll(unassigned);
                System.err.println("[DEBUG] PURCHASE userId: " + userId);
                for (IndentRequest ir : returnedIndents) {
                    System.err.println("[DEBUG] PURCHASE Indent: id=" + ir.getId() + ", status=" + ir.getStatus() + ", purchase.id=" + (ir.getPurchase() != null ? ir.getPurchase().getId() : null));
                }
            }

            default -> throw new RuntimeException("Invalid role: " + role);
        }

        System.err.println("  149   ajsdkfjaklsjfieeinfiakdheu ");
        return ResponseEntity.ok(returnedIndents);
    }

    // DEBUG: List all indents with SENT_BACK_TO_PURCHASE status and their purchase user
    @GetMapping("/debug/purchase-indents")
    public ResponseEntity<?> debugPurchaseIndents() {
        List<IndentRequest> indents = indentRequestRepository.findByStatusIn(List.of(IndentStatus.SENT_BACK_TO_PURCHASE));
        for (IndentRequest ir : indents) {
            System.err.println("[DEBUG] Indent id=" + ir.getId() + ", status=" + ir.getStatus() + ", purchase.id=" + (ir.getPurchase() != null ? ir.getPurchase().getId() : null));
        }
        return ResponseEntity.ok(indents);
    }

}
