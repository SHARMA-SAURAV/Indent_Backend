package com.example.demo.controller;

import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.PurchaseReview;
import com.example.demo.model.User;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.PurchaseReviewRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/indent/purchase")
public class PurchaseController {

    @Autowired
    private IndentRequestRepository indentRequestRepository;

    @Autowired
    private PurchaseReviewRepository purchaseReviewRepository;

    @Autowired
    private EmailService emailService;

    // POST: Add Purchase Review
    @PostMapping("/add-review")
    public ResponseEntity<?> addReview(@RequestBody Map<String, String> request,
                                       Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId"));
        String comment = request.get("comment");
        String reviewer = authentication.getName();

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

//        PurchaseReview review = new PurchaseReview(reviewer, comment, indent);

        PurchaseReview review = new PurchaseReview();
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setIndentRequest(indent);
//        review.setReviewedAt(LocalDateTime.now());
        review.setReviewDate(LocalDateTime.now());
        purchaseReviewRepository.save(review);

        return ResponseEntity.ok(Map.of("message", "Review added successfully"));
    }

    // POST: Complete the Indent (Only if Inward Entry is true)
    @PostMapping("/complete")
    public ResponseEntity<?> completeIndent(@RequestBody Map<String, Object> request,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_PURCHASE) {
            return ResponseEntity.badRequest().body("Indent not in Purchase stage");
        }

        if (!indent.isInwardEntryGenerated()) {
            return ResponseEntity.badRequest().body("Inward Entry must be generated before approval");
        }

        indent.setRemarkByPurchase(remark);
        indent.setStatus(IndentStatus.WAITING_FOR_USER_CONFIRMATION);
        indent.setPurchaseCompletionDate(LocalDateTime.now());

        indentRequestRepository.save(indent);

        User user = indent.getRequestedBy();
        if (user != null && user.getEmail() != null) {
            String emailBody = "Hello " + user.getUsername() + ",\n\n" +
                    "Your indent has been approved by Purchase and is now pending your inspection.\n" +
                    "Indent ID: " + indentId + "\n\n" +
                    "Regards,\nIndent System";
            emailService.sendEmail(user.getEmail(), "Indent Approved by Purchase", emailBody);
        }

        return ResponseEntity.ok(Map.of("message", "Indent marked as WAITING_FOR_USER_CONFIRMATION"));
    }

    // GET: All Reviews for a given indent
    @GetMapping("/{indentId}/reviews")
    public ResponseEntity<?> getReviewsForIndent(@PathVariable Long indentId) {
        List<PurchaseReview> reviews = purchaseReviewRepository.findByIndentRequestId(indentId);
        return ResponseEntity.ok(reviews);
    }

    // Existing Rejection Endpoint
    @PostMapping("/reject")
    public ResponseEntity<?> rejectIndent(@RequestBody Map<String, Object> request,
                                          Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_PURCHASE) {
            return ResponseEntity.badRequest().body("Indent not in Purchase stage");
        }

        indent.setRemarkByPurchase(remark);
        indent.setStatus(IndentStatus.PURCHASE_REJECTED);
        indent.setPurchaseCompletionDate(LocalDateTime.now());

        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Indent rejected by Purchase"));
    }

    // GET: Indents pending for Purchase
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingIndentsForPurchase(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        List<IndentRequest> pendingIndents = indentRequestRepository.findByStatus(IndentStatus.PENDING_PURCHASE);
        return ResponseEntity.ok(pendingIndents);
    }
    // Add this method to your PurchaseController class

    @PostMapping("/toggle-inward-entry")
    public ResponseEntity<?> toggleInwardEntry(@RequestBody Map<String, Object> request,
                                               Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        Boolean inwardEntryGenerated = (Boolean) request.get("inwardEntryGenerated");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_PURCHASE) {
            return ResponseEntity.badRequest().body("Indent not in Purchase stage");
        }

        indent.setInwardEntryGenerated(inwardEntryGenerated);
        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of(
                "message", "Inward entry status updated successfully",
                "inwardEntryGenerated", inwardEntryGenerated
        ));
    }

    @GetMapping("/tracking")
    public ResponseEntity<?> getPurchaseTrackingIndents(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        // Fetch all indents with associated users (requestedBy, FLA, SLA, etc.)
        List<IndentRequest> allIndents = indentRequestRepository.findAllWithUser();

        // Filter indents that are relevant for Purchase (status at or after PENDING_PURCHASE)
        List<IndentRequest> purchaseRelevant = allIndents.stream()
                .filter(indent -> indent.getStatus().ordinal() >= IndentStatus.PENDING_PURCHASE.ordinal())
                .toList();

        return ResponseEntity.ok(purchaseRelevant);
    }

}
