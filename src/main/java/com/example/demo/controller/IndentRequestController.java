//package com.example.demo.controller;
//
////package com.indentmanagement.controller;
////
////import com.indentmanagement.model.IndentRequest;
////import com.indentmanagement.model.IndentRemark;
////import com.indentmanagement.service.IndentRequestService;
//import com.example.demo.model.IndentRemark;
//import com.example.demo.model.IndentRequest;
//import com.example.demo.service.IndentRequestService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/indent")
//public class IndentRequestController {
//
//    private final IndentRequestService indentRequestService;
//
//    public IndentRequestController(IndentRequestService indentRequestService) {
//        this.indentRequestService = indentRequestService;
//    }
//
//    // User creates an indent request
//    @PostMapping("/create")
//    public ResponseEntity<IndentRequest> createIndent(@RequestBody Map<String, Object> request) {
//        Long userId = Long.valueOf(request.get("userId").toString());
//        String itemName = (String) request.get("itemName");
//        int quantity = Integer.parseInt(request.get("quantity").toString());
//        String description = (String) request.get("description");
//        Long flaId = Long.valueOf(request.get("flaId").toString());
//        IndentRequest indent = indentRequestService.createIndentRequest(userId, itemName, quantity, description, flaId);
//        return ResponseEntity.ok(indent);
//    }
//
//    // FLA assigns SLA
//    @PostMapping("/assign-sla")
//    public ResponseEntity<IndentRequest> assignSLA(@RequestBody Map<String, Object> request) {
//        Long indentId = Long.valueOf(request.get("indentId").toString());
//        Long slaId = Long.valueOf(request.get("slaId").toString());
//        IndentRequest indent = indentRequestService.assignSLA(indentId, slaId);
//        return ResponseEntity.ok(indent);
//    }
//
//    // Add remark
//    @PostMapping("/add-remark")
//    public ResponseEntity<IndentRemark> addRemark(@RequestBody Map<String, Object> request) {
//        Long indentId = Long.valueOf(request.get("indentId").toString());
//        Long userId = Long.valueOf(request.get("userId").toString());
//        String remark = (String) request.get("remark");
//        IndentRemark indentRemark = indentRequestService.addRemark(indentId, userId, remark);
//        return ResponseEntity.ok(indentRemark);
//    }
//}
//







//package com.example.demo.controller;

//import com.example.indent.model.Indent;
//import com.example.indent.model.User;
//import com.example.indent.repository.IndentRepository;
//import com.example.indent.repository.UserRepository;
//import com.example.indent.service.EmailService;

//import com.example.demo.model.IndentRequest;
//import com.example.demo.model.IndentStatus;
//import com.example.demo.model.User;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/indents")
//public class IndentRequestController {
//
//    @Autowired
//    private IndentRequestController indentRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    // ✅ 1. Create a new indent
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> createIndent(@RequestBody IndentRequest indent) {
//        Optional<User> requester = userRepository.findById((long) indent.getRequestedBy().getId());
//        if (requester.isEmpty()) {
//            return ResponseEntity.badRequest().body("Invalid requester ID");
//        }
//
//        indent.setStatus(IndentStatus.valueOf("PENDING_FLA"));
//        indentRepository.save(indent);
//
//        return ResponseEntity.ok("Indent created successfully!");
//    }
//
//    // ✅ 2. Get all indents (for Admin)
//    @GetMapping("/all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<Indent> getAllIndents() {
//        return indentRepository.findAll();
//    }
//
//    // ✅ 3. Assign an FLA to the indent
//    @PutMapping("/{id}/assign-fla")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> assignFLA(@PathVariable Long id, @RequestBody User fla) {
//        Indent indent = indentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Indent not found"));
//
//        Optional<User> flaUser = userRepository.findById(fla.getId());
//        if (flaUser.isEmpty() || !flaUser.get().getRole().equals("FLA")) {
//            return ResponseEntity.badRequest().body("Invalid FLA selection");
//        }
//
//        indent.setFla(flaUser.get());
//        indent.setStatus("PENDING_SLA");
//        indentRepository.save(indent);
//
//        // Notify FLA
//        emailService.sendEmail(flaUser.get().getEmail(), "Indent Assigned to You",
//                "An indent has been assigned to you for approval.");
//
//        return ResponseEntity.ok("FLA assigned and notified");
//    }
//
//    // ✅ 4. Assign an SLA to the indent
//    @PutMapping("/{id}/assign-sla")
//    @PreAuthorize("hasRole('FLA')")
//    public ResponseEntity<?> assignSLA(@PathVariable Long id, @RequestBody User sla) {
//        Indent indent = indentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Indent not found"));
//
//        Optional<User> slaUser = userRepository.findById(sla.getId());
//        if (slaUser.isEmpty() || !slaUser.get().getRole().equals("SLA")) {
//            return ResponseEntity.badRequest().body("Invalid SLA selection");
//        }
//
//        indent.setSla(slaUser.get());
//        indent.setStatus("PENDING_STORE");
//        indentRepository.save(indent);
//
//        // Notify SLA
//        emailService.sendEmail(slaUser.get().getEmail(), "Indent Assigned to You",
//                "An indent has been assigned to you for further processing.");
//
//        return ResponseEntity.ok("SLA assigned and notified");
//    }
//
//    // ✅ 5. Update indent status (Store, Finance, Purchase approval)
//    @PutMapping("/{id}/update-status")
//    @PreAuthorize("hasAnyRole('STORE', 'FINANCE', 'PURCHASE')")
//    public ResponseEntity<?> updateIndentStatus(@PathVariable Long id, @RequestBody String status) {
//        Indent indent = indentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Indent not found"));
//
//        indent.setStatus(status);
//        indentRepository.save(indent);
//
//        // Notify requester
//        emailService.sendEmail(indent.getRequestedBy().getEmail(), "Indent Status Updated",
//                "Your indent #" + indent.getId() + " status is now: " + status);
//
//        return ResponseEntity.ok("Indent status updated and requester notified");
//    }
//
//    // ✅ 6. Add remark to an indent
//    @PostMapping("/{id}/add-remark")
//    @PreAuthorize("hasAnyRole('STORE', 'FINANCE', 'PURCHASE')")
//    public ResponseEntity<?> addRemark(@PathVariable Long id, @RequestBody String remark) {
//        Indent indent = indentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Indent not found"));
//
//        indent.getRemarks().add(remark);
//        indentRepository.save(indent);
//
//        // Notify requester about the remark
//        emailService.sendEmail(indent.getRequestedBy().getEmail(), "New Remark Added",
//                "A new remark has been added to your indent #" + indent.getId() + ": " + remark);
//
//        return ResponseEntity.ok("Remark added and requester notified");
//    }
//
//    // ✅ 7. Get indent details
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getIndentById(@PathVariable Long id) {
//        Optional<Indent> indent = indentRepository.findById(id);
//        if (indent.isEmpty()) {
//            return ResponseEntity.badRequest().body("Indent not found");
//        }
//        return ResponseEntity.ok(indent.get());
//    }
//}


package com.example.demo.controller;

import com.example.demo.dto.IndentDTO;
import com.example.demo.dto.IndentRequestDTO;
import com.example.demo.model.IndentRemark;
import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.User;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.IndentRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/indent")
public class IndentRequestController {

    private final IndentRequestRepository indentRequestRepository;
    private final IndentRequestService indentRequestService;
    private  final UserRepository userRepository;

    public IndentRequestController(IndentRequestService indentRequestService,UserRepository userRepository, IndentRequestRepository indentRequestRepository) {
        this.indentRequestService = indentRequestService;
        this.userRepository= userRepository;
        this.indentRequestRepository = indentRequestRepository;
    }
//System.out.println("we are outside the create indent");
//    // ✅ 1. Create an indent request (User selects FLA)
//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/create")
//    public ResponseEntity<IndentRequest> createIndent(@RequestBody Map<String, Object> request) {
//
//        System.err.println("we are in create indent");
//
//        Long userId = Long.valueOf(request.get("userId").toString());
//        String itemName = (String) request.get("itemName");
//        int quantity = Integer.parseInt(request.get("quantity").toString());
//        String description = (String) request.get("description");
//        Long flaId = Long.valueOf(request.get("flaId").toString());
//
//        IndentRequest indent = indentRequestService.createIndentRequest(userId, itemName, quantity, description, flaId);
//        return ResponseEntity.ok(indent);
//    }



    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createIndent(@Valid @RequestBody Map<String, Object> request,
                                          Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Get authenticated user1
//        User user = (User) authentication.getPrincipal();

        // Use user.getId() instead of getting from request

        String itemName = (String) request.get("itemName");
//        print itemname
        System.err.println("Item Name: " + itemName);
        int quantity = (int) request.get("quantity");
        //print quantity
        System.err.println("Quantity: " + quantity);
        Long perPieceCost = Long.valueOf((request.get("perPieceCost").toString()));
        //print perPieceCost
        System.err.println("Per Piece Cost: " + perPieceCost);
        String description = (String) request.get("description");
        //print description
        System.err.println("Description: " + description);

        Long flaId = Long.valueOf(request.get("flaId").toString());
        //print flaID
        System.err.println("----------------------------------------------------------------------------------------: " );
        System.err.println("FLA ID: " + request.get("flaId").toString());
//        Long flaId = Long.valueOf(request.get("flaId").toString());

        IndentRequest indent = indentRequestService.createIndentRequest(
                flaId, itemName, quantity,perPieceCost, description, flaId);

        return ResponseEntity.ok(indent);
    }




    // IndentController.java
    @GetMapping("/fla/pending")
    public ResponseEntity<?> getPendingIndentsForFLA(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User flaUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("FLA user not found"));

        List<IndentRequest> pendingIndents = indentRequestRepository
                .findByFlaAndStatus(flaUser, IndentStatus.PENDING_FLA);

        return ResponseEntity.ok(pendingIndents);
    }


    // IndentController.java
    @PostMapping("/fla/approve")
    public ResponseEntity<?> approveIndentAsFLA(@RequestBody Map<String, Object> request,
                                                Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");
        Long slaId = Long.valueOf(request.get("slaId").toString());

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_FLA) {
            return ResponseEntity.badRequest().body("Indent not in FLA stage");
        }

        // update status, remark, sla, etc.
        indent.setRemarkByFla(remark);
        indent.setSla(userRepository.findById(slaId).orElseThrow(() -> new RuntimeException("SLA not found")));
        indent.setStatus(IndentStatus.PENDING_SLA);
        indent.setFlaApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to SLA"));
    }

    // IndentController.java
    @GetMapping("/sla/pending")
    public ResponseEntity<?> getPendingIndentsForSLA(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User slaUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("SLA user not found"));

        List<IndentRequest> pendingIndents = indentRequestRepository
                .findBySlaAndStatus(slaUser, IndentStatus.PENDING_SLA);

        return ResponseEntity.ok(pendingIndents);
    }


    // IndentController.java
    // IndentController.java
    @PostMapping("/sla/approve")
    public ResponseEntity<?> approveIndentAsSLA(@RequestBody Map<String, Object> request,
                                                Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        // Fetch the indent request
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_SLA) {
            return ResponseEntity.badRequest().body("Indent not in SLA stage");
        }

        // Set SLA's remark
        indent.setRemarkBySla(remark);
        indent.setStatus(IndentStatus.PENDING_STORE);  // Move to Store stage
        indent.setSlaApprovalDate(LocalDateTime.now());

        // Assign Store (Saurav) directly (userId = 12)
        User storeUser = userRepository.findById(12L)
                .orElseThrow(() -> new RuntimeException("Store user not found"));
        indent.setStore(storeUser);  // Assign Saurav as the store user

        // Save the updated indent request
        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to Store"));
    }




    // IndentController.java
    @PostMapping("/store/approve")
    public ResponseEntity<?> approveIndentAsStore(@RequestBody Map<String, Object> request,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        // Fetch the indent request
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_STORE) {
            return ResponseEntity.badRequest().body("Indent not in Store stage");
        }

        // Set Store's remark and change status to Finance stage
        indent.setRemarkByStore(remark);
        indent.setStatus(IndentStatus.PENDING_FINANCE);  // Move to Finance stage
        indent.setStoreApprovalDate(LocalDateTime.now());

        // Save the updated indent request
        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to Finance"));
    }




    // IndentController.java
    @GetMapping("/store/pending")
    public ResponseEntity<?> getPendingIndentsForStore(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        // Fetch all pending indents for Store (PENDING_STORE)
        List<IndentRequest> pendingIndents = indentRequestRepository.findByStatus(IndentStatus.PENDING_STORE);

        if (pendingIndents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Prepare the response data
        List<IndentRequestDTO> indentDTOs = pendingIndents.stream()
                .map(indent -> new IndentRequestDTO(
                        indent.getId(),
                        indent.getItemName(),
                        indent.getQuantity(),
                        indent.getPerPieceCost(),
                        indent.getDescription(),
                        indent.getFla().getUsername(), // FLA's username
                        indent.getRemarkBySla(),
                        indent.getStatus().name()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(indentDTOs);
    }



    @GetMapping("/finance/pending")
    public ResponseEntity<?> getPendingIndentsForFinance(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        List<IndentRequest> pendingIndents = indentRequestRepository.findByStatus(IndentStatus.PENDING_FINANCE);
        return ResponseEntity.ok(pendingIndents);
    }




    @PostMapping("/finance/approve")
    public ResponseEntity<?> approveIndentAsFinance(@RequestBody Map<String, Object> request,
                                                    Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_FINANCE) {
            return ResponseEntity.badRequest().body("Indent not in Finance stage");
        }

        // Set remark and update status
        indent.setRemarkByFinance(remark);
        indent.setStatus(IndentStatus.PENDING_PURCHASE);  // Move to Purchase
        indent.setFinanceApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to Purchase"));
    }


    // Get all pending purchase indents
    @GetMapping("/purchase/pending")
    public ResponseEntity<?> getPendingIndentsForPurchase(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        List<IndentRequest> pendingIndents = indentRequestRepository.findByStatus(IndentStatus.PENDING_PURCHASE);
        return ResponseEntity.ok(pendingIndents);
    }


    // Complete the indent (final approval)
    @PostMapping("/purchase/complete")
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

        indent.setRemarkByPurchase(remark);
        indent.setStatus(IndentStatus.COMPLETED);
        indent.setPurchaseCompletionDate(LocalDateTime.now());

        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Indent marked as COMPLETED by Purchase"));
    }







    @PutMapping("/{indentId}/approve-fla")
    public ResponseEntity<IndentRequest> approveByFLA(
            @PathVariable Long indentId,
            @RequestParam Long slaId,
            @RequestBody String remark
    ) {
        IndentRequest indent = indentRequestService.approveByFLA(indentId, slaId, remark);
        return ResponseEntity.ok(indent);
    }

    @PutMapping("/{indentId}/approve-sla")
    public ResponseEntity<IndentRequest> approveBySLA(
            @PathVariable Long indentId,
            @RequestBody String remark
    ) {
        IndentRequest indent = indentRequestService.approveBySLA(indentId, remark);
        return ResponseEntity.ok(indent);
    }

    @PutMapping("/{indentId}/approve-store")
    public ResponseEntity<IndentRequest> approveByStore(
            @PathVariable Long indentId,
            @RequestBody String remark
    ) {
        IndentRequest indent = indentRequestService.approveByStore(indentId, remark);
        return ResponseEntity.ok(indent);
    }
    @PutMapping("/{indentId}/approve-finance")
    public ResponseEntity<IndentRequest> approveByFinance(
            @PathVariable Long indentId,
            @RequestBody String remark
    ) {
        IndentRequest indent = indentRequestService.approveByFinance(indentId, remark);
        return ResponseEntity.ok(indent);
    }
    @PutMapping("/{indentId}/approve-purchase")
    public ResponseEntity<IndentRequest> approveByPurchase(
            @PathVariable Long indentId,
            @RequestBody String remark
    ) {
        IndentRequest indent = indentRequestService.approveByPurchase(indentId, remark);
        return ResponseEntity.ok(indent);
    }


// Similar endpoints for store, finance, purchase





    // ✅ 2. FLA assigns SLA
    @PutMapping("/{indentId}/assign-sla/{slaId}")
    public ResponseEntity<IndentRequest> assignSLA(@PathVariable Long indentId, @PathVariable Long slaId) {
        IndentRequest indent = indentRequestService.assignSLA(indentId, slaId);
        return ResponseEntity.ok(indent);
    }

    // ✅ 3. SLA forwards to Store
    @PutMapping("/{indentId}/forward-store")
    public ResponseEntity<IndentRequest> forwardToStore(@PathVariable Long indentId) {
        IndentRequest indent = indentRequestService.forwardToStore(indentId);
        return ResponseEntity.ok(indent);
    }

    // ✅ 4. Store forwards to Finance
    @PutMapping("/{indentId}/forward-finance")
    public ResponseEntity<IndentRequest> forwardToFinance(@PathVariable Long indentId) {
        IndentRequest indent = indentRequestService.forwardToFinance(indentId);
        return ResponseEntity.ok(indent);
    }

    // ✅ 5. Finance forwards to Purchase
    @PutMapping("/{indentId}/forward-purchase")
    public ResponseEntity<IndentRequest> forwardToPurchase(@PathVariable Long indentId) {
        IndentRequest indent = indentRequestService.forwardToPurchase(indentId);
        return ResponseEntity.ok(indent);
    }

    // ✅ 6. Purchase completes the indent
    @PutMapping("/{indentId}/complete")
    public ResponseEntity<IndentRequest> completeIndent(@PathVariable Long indentId) {
        IndentRequest indent = indentRequestService.completeIndent(indentId);
        return ResponseEntity.ok(indent);
    }

    // ✅ 7. Add a remark
    @PostMapping("/{indentId}/add-remark")
    public ResponseEntity<IndentRemark> addRemark(@PathVariable Long indentId, @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String remark = (String) request.get("remark");

        IndentRemark indentRemark = indentRequestService.addRemark(indentId, userId, remark);
        return ResponseEntity.ok(indentRemark);
    }

    // ✅ 8. Get all indents created by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IndentRequest>> getUserIndents(@PathVariable Long userId) {
        //print userID
        return ResponseEntity.ok(indentRequestService.getFLAIndents(userId));
    }

    // ✅ 9. Get all indents assigned to an FLA
    @GetMapping("/fla/{flaId}")
    public ResponseEntity<List<IndentRequest>> getFLAIndents(@PathVariable Long flaId) {
        return ResponseEntity.ok(indentRequestService.getFLAIndents(flaId));
    }

    // ✅ 10. Get all indents assigned to an SLA
    @GetMapping("/sla/{slaId}")
    public ResponseEntity<List<IndentRequest>> getSLAIndents(@PathVariable Long slaId) {
        return ResponseEntity.ok(indentRequestService.getSLAIndents(slaId));
    }

    // ✅ 11. Get all remarks for an indent
    @GetMapping("/{indentId}/remarks")
    public ResponseEntity<List<IndentRemark>> getIndentRemarks(@PathVariable Long indentId) {
        return ResponseEntity.ok(indentRequestService.getIndentRemarks(indentId));
    }

    // ✅ 12. Get indent details by ID
    @GetMapping("/{indentId}")
    public ResponseEntity<IndentRequest> getIndentById(@PathVariable Long indentId) {
        return indentRequestService.getIndentById(indentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
}
