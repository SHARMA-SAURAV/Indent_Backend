package com.example.demo.controller;
import com.example.demo.dto.IndentRequestDTO;
//import com.example.demo.dto.ReviewRequest;
import com.example.demo.mapper.IndentRequestMapper;
import com.example.demo.model.*;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.PurchaseReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.EmailService;
import com.example.demo.service.IndentRequestService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/indent")
public class IndentRequestController {

    private final IndentRequestRepository indentRequestRepository;
    private final IndentRequestService indentRequestService;
    private  final UserRepository userRepository;
    private final EmailService emailService;
    private final PurchaseReviewRepository purchaseReviewRepository;
    public IndentRequestController(IndentRequestService indentRequestService,UserRepository userRepository, PurchaseReviewRepository purchaseReviewRepository,IndentRequestRepository indentRequestRepository, EmailService emailService) {
        this.indentRequestService = indentRequestService;
        this.userRepository= userRepository;
        this.indentRequestRepository = indentRequestRepository;
        this.emailService=emailService;
        this.purchaseReviewRepository = purchaseReviewRepository;
    }
    public  void emailrejectedindent(String role, IndentRequest indent, Long indentId){
        User user =indent.getRequestedBy();
        System.err.println("USER  --"+user);
        if(user!= null && user.getEmail()!= null){
            String userEmail = user.getEmail();
            String Username= user.getUsername();
            String emailBody = "Hello " +Username + ",\n\n" +
                    "The indent request has been rejected by " + role+"\n"+
                    "Indent ID: " + indentId + "\n" +
                    "Project Name: " + indent.getProjectName() + "\n" +
                    "Item Name: " + indent.getItemName() + "\n" +
                    "Quantity: " + indent.getQuantity() + "\n" +
                    "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                    "Department: " + indent.getDepartment() + "\n" +
                    "Description: " + indent.getDescription() + "\n\n" +
                    "Best regards,\n" +
                    "Your Indent Management System";
            System.err.println(emailBody);
            System.err.println("email of rejected mail"+userEmail);
                emailService.sendEmail(userEmail, " Indent Rejected", emailBody);
        }
    }


//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/create")
//    public ResponseEntity<?> createIndent(@Valid @RequestBody Map<String, Object> request,
//                                          Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new AccessDeniedException("User not authenticated");
//        }
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        User user = userRepository.findByUsername(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String recipientType =(String) request.get("recipientType");
//        Long recipientId=Long.valueOf(request.get("recipientId").toString());
//        String projectName = (String) request.get("projectName");
//        System.err.println("Project Name: " + projectName);
//        String itemName = (String) request.get("itemName");
////        print itemname
//        System.err.println("Item Name: " + itemName);
//        int quantity = (int) request.get("quantity");
//        //print quantity
//        System.err.println("Quantity: " + quantity);
//        Long perPieceCost = Long.valueOf((request.get("perPieceCost").toString()));
//        //print perPieceCost
//        System.err.println("Per Piece Cost: " + perPieceCost);
//        String description = (String) request.get("description");
//        //print description
//        System.err.println("Description: " + description);
//        Double totalCost = Double.valueOf(request.get("totalCost").toString());
//        String purpose = (String) request.get("purpose");
//        String department = (String) request.get("department");
//        String specificationModelDetails = (String) request.get("specificationModelDetails");
//
//        IndentRequest indent = indentRequestService.createIndentRequest(
//                (long) user.getId(), itemName, quantity, perPieceCost, description,
//                recipientType, recipientId, projectName, totalCost, purpose,
//                department, specificationModelDetails);
//
//        return ResponseEntity.ok(indent);
//    }




@PreAuthorize("hasRole('USER')")
@PostMapping("/create-multiple")
public ResponseEntity<?> createMultipleIndents(@Valid @RequestBody Map<String, Object> request,
                                               Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new AccessDeniedException("User not authenticated");
    }

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    try {
        // Extract common fields
        String recipientType = (String) request.get("recipientType");
        Long recipientId = Long.valueOf(request.get("recipientId").toString());
        String projectName = (String) request.get("projectName");
        String purpose = (String) request.get("purpose");
        String department = (String) request.get("department");

        // Extract items array
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) request.get("items");

        if (itemsList == null || itemsList.isEmpty()) {
            return ResponseEntity.badRequest().body("At least one item is required");
        }

        List<IndentRequest> createdIndents = new ArrayList<>();

        // Process each item and create separate indent requests
        for (Map<String, Object> itemData : itemsList) {
            String itemName = (String) itemData.get("itemName");
            String category = (String) itemData.get("category");
            int quantity = (int) itemData.get("quantity");
            Long perPieceCost = Long.valueOf(itemData.get("perPieceCost").toString());
            String description = (String) itemData.get("description");
            String specificationModelDetails = (String) itemData.get("specificationModelDetails");
            Double totalCost = Double.valueOf(itemData.get("totalCost").toString());

            // Handle file upload information
            String fileName = (String) itemData.get("fileName");
            String fileUrl = (String) itemData.get("fileUrl");
            String fileType = (String) itemData.get("fileType");

            System.err.println("Creating indent for item: " + itemName + ", Category: " + category);

            IndentRequest indent = indentRequestService.createIndentRequestWithCategory(
                    (long) user.getId(), itemName, category, quantity, perPieceCost,
                    description, recipientType, recipientId, projectName, totalCost,
                    purpose, department, specificationModelDetails, fileName, fileUrl, fileType);

            createdIndents.add(indent);
        }

        // Return response with created indents
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully created " + createdIndents.size() + " indent requests");
        response.put("indents", createdIndents);
        response.put("totalIndents", createdIndents.size());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.err.println("Error creating multiple indents: " + e.getMessage());
        return ResponseEntity.badRequest().body("Error creating indents: " + e.getMessage());
    }
}

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-draft")
    public ResponseEntity<?> createDraftIndents(@Valid @RequestBody Map<String, Object> request,
                                                Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Save items as draft (temporary storage)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsList = (List<Map<String, Object>>) request.get("items");

            // You can store this in a temporary table or cache for later processing
            // For now, returning the processed data for frontend confirmation

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Draft saved successfully");
            response.put("itemCount", itemsList.size());
            response.put("items", itemsList);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving draft: " + e.getMessage());
        }
    }

    // Keep the original single indent creation for backward compatibility
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

        String recipientType = (String) request.get("recipientType");
        Long recipientId = Long.valueOf(request.get("recipientId").toString());
        String projectName = (String) request.get("projectName");
        String itemName = (String) request.get("itemName");
        String category = (String) request.get("category"); // New field
        int quantity = (int) request.get("quantity");
        Long perPieceCost = Long.valueOf((request.get("perPieceCost").toString()));
        String description = (String) request.get("description");
        Double totalCost = Double.valueOf(request.get("totalCost").toString());
        String purpose = (String) request.get("purpose");
        String department = (String) request.get("department");
        String specificationModelDetails = (String) request.get("specificationModelDetails");

        // File upload fields
        String fileName = (String) request.get("fileName");
        String fileUrl = (String) request.get("fileUrl");
        String fileType = (String) request.get("fileType");

        System.err.println("Project Name: " + projectName);
        System.err.println("Item Name: " + itemName);
        System.err.println("Category: " + category);
        System.err.println("Quantity: " + quantity);
        System.err.println("Per Piece Cost: " + perPieceCost);
        System.err.println("Description: " + description);
        System.err.println("File Name: " + fileName);

        IndentRequest indent = indentRequestService.createIndentRequestWithCategory(
                (long) user.getId(), itemName, category, quantity, perPieceCost, description,
                recipientType, recipientId, projectName, totalCost, purpose,
                department, specificationModelDetails, fileName, fileUrl, fileType);

        return ResponseEntity.ok(indent);
    }

    @PreAuthorize("hasAnyRole('USER', 'FLA', 'SLA', 'FINANCE', 'PURCHASE', 'STORE')")
    @GetMapping("/by-category/{category}")
    public ResponseEntity<?> getIndentsByCategory(@PathVariable String category,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        try {
            List<IndentRequest> indents = indentRequestService.getIndentsByCategory(category, userRole);

            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("userRole", userRole);
            response.put("indents", indents);
            response.put("totalCount", indents.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching indents by category: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'FLA', 'SLA', 'FINANCE', 'PURCHASE', 'STORE')")
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<String> categories = indentRequestRepository.findAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching categories: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'FLA', 'SLA', 'FINANCE', 'PURCHASE', 'STORE')")
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<?> getIndentsByBatch(@PathVariable String batchId) {
        try {
            List<IndentRequest> indents = indentRequestRepository.findByBatchIdOrderBySequence(batchId);

            Map<String, Object> response = new HashMap<>();
            response.put("batchId", batchId);
            response.put("indents", indents);
            response.put("totalCount", indents.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching batch indents: " + e.getMessage());
        }
    }
// Add this file upload endpoint to your controller

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Check file size (e.g., max 10MB)
            long maxFileSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest().body("File size exceeds maximum limit of 10MB");
            }

            // Validate file types (adjust as needed)
            String contentType = file.getContentType();
            List<String> allowedTypes = Arrays.asList(
                    "application/pdf",
                    "image/jpeg",
                    "image/png",
                    "image/jpg",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            if (!allowedTypes.contains(contentType)) {
                return ResponseEntity.badRequest().body("File type not supported");
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Define upload directory (adjust path as needed)
            String uploadDir = "uploads/indent-files/";
            Path uploadPath = Paths.get(uploadDir);

            // Create directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file to disk
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create file URL (adjust base URL as needed)
            String fileUrl = "/api/files/" + uniqueFileName;

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileName", originalFileName);
            response.put("uniqueFileName", uniqueFileName);
            response.put("fileUrl", fileUrl);
            response.put("fileType", contentType);
            response.put("fileSize", file.getSize());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error processing file upload: " + e.getMessage());
        }
    }

    // Add this endpoint to serve uploaded files
    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            String uploadDir = "uploads/indent-files/";
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

        User slaUser=userRepository.findById(slaId).orElseThrow(()-> new RuntimeException("SLA user not found"));
        // update status, remark, sla, etc.
        indent.setRemarkByFla(remark);
        indent.setSla(userRepository.findById(slaId).orElseThrow(() -> new RuntimeException("SLA not found")));
        indent.setStatus(IndentStatus.PENDING_SLA);
        indent.setFlaApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);
        // Send email notification to SLA
        String emailBody = "Hello " + slaUser.getUsername() + ",\n\n" +
                "An indent request has been approved by FLA and is now pending your approval.\n" +
                "Indent ID: " + indentId + "\n" +
                "Project Name: " + indent.getProjectName() + "\n" +
                "Item Name: " + indent.getItemName() + "\n" +
                "Quantity: " + indent.getQuantity() + "\n" +
                "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                "Department: " + indent.getDepartment() + "\n" +
                "Description: " + indent.getDescription() + "\n\n" +
                "Please log in to the system to review the request.\n\n" +
                "Best regards,\n" +
                "Your Indent Management System";
        emailService.sendEmail(slaUser.getEmail(), "Indent Approval Required", emailBody);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to SLA"));
    }


    // IndentController.java

    @PostMapping("/fla/reject")
    public ResponseEntity<?> rejectIndentAsFLA(@RequestBody Map<String, Object> request,
                                               Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        // Get authenticated user
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User flaUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("FLA user not found"));

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        // Ensure the indent is in correct stage
        if (indent.getStatus() != IndentStatus.PENDING_FLA) {
            return ResponseEntity.badRequest().body("Indent not in FLA stage");
        }

        // Perform rejection
        indent.setRemarkByFla(remark);
        indent.setStatus(IndentStatus.REJECTED_BY_FLA);
        indent.setFlaApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);
        System.err.println("before rejectiopn");
        emailrejectedindent("FLA", indent, indentId);
        System.err.println("after rejectiopn");




        return ResponseEntity.ok(Map.of("message", "Indent rejected by FLA"));
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
        User storeUser = userRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Store user not found"));
        indent.setStore(storeUser);  // Assign Saurav as the store user

        // Save the updated indent request
        indentRequestRepository.save(indent);


        String emailBody = "Hello " + "Store User" + ",\n\n" +
                "An indent request has been approved by SLA and is now pending your approval.\n" +
                "Indent ID: " + indentId + "\n" +
                "Project Name: " + indent.getProjectName() + "\n" +
                "Item Name: " + indent.getItemName() + "\n" +
                "Quantity: " + indent.getQuantity() + "\n" +
                "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                "Department: " + indent.getDepartment() + "\n" +
                "Description: " + indent.getDescription() + "\n\n" +
                "Please log in to the system to review the request.\n\n" +
                "Best regards,\n" +
                "Your Indent Management System";
        emailService.sendEmail("storeuser@gmail.com", "Indent Approval Required", emailBody);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to Store"));
    }


    @PostMapping("/sla/reject")
    public ResponseEntity<?> rejectIndentAsSLA(@RequestBody Map<String, Object> request,
                                               Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User slaUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("SLA user not found"));

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_SLA) {
            return ResponseEntity.badRequest().body("Indent not in SLA stage");
        }

        indent.setRemarkBySla(remark);
        indent.setStatus(IndentStatus.REJECTED_BY_SLA);
        indent.setSlaApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);
        emailrejectedindent("SLA", indent, indentId);
        return ResponseEntity.ok(Map.of("message", "Indent rejected by SLA"));
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

        String emailBody = "Hello " + "Finance Department" + ",\n\n" +
                "An indent request has been approved by Store and is now pending your approval.\n" +
                "Indent ID: " + indentId + "\n" +
                "Project Name: " + indent.getProjectName() + "\n" +
                "Item Name: " + indent.getItemName() + "\n" +
                "Quantity: " + indent.getQuantity() + "\n" +
                "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                "Department: " + indent.getDepartment() + "\n" +
                "Description: " + indent.getDescription() + "\n\n" +
                "Please log in to the system to review the request.\n\n" +
                "Best regards,\n" +
                "Your Indent Management System";
        emailService.sendEmail("financeDepartment@gmail.com", "Indent Approval Required", emailBody);

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
                        indent.getFla() != null? indent.getFla().getUsername():null, // FLA's username
                        indent.getSla() !=null? indent.getSla().getUsername(): null,
                        indent.getRemarkBySla(),
                        indent.getStatus().name()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(indentDTOs);
    }


    @PostMapping("/store/reject")
    public ResponseEntity<?> rejectIndentAsStore(@RequestBody Map<String, Object> request,
                                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_STORE) {
            return ResponseEntity.badRequest().body("Indent not in Store stage");
        }

        indent.setRemarkByStore(remark);
        indent.setStatus(IndentStatus.REJECTED_BY_STORE);
        indent.setStoreApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);
        emailrejectedindent("Store", indent, indentId);
        return ResponseEntity.ok(Map.of("message", "Indent rejected by Store"));
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

        String emailBody = "Hello " + "Purchase User" + ",\n\n" +
                "An indent request has been approved by SLA and is now pending your approval.\n" +
                "Indent ID: " + indentId + "\n" +
                "Project Name: " + indent.getProjectName() + "\n" +
                "Item Name: " + indent.getItemName() + "\n" +
                "Quantity: " + indent.getQuantity() + "\n" +
                "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                "Department: " + indent.getDepartment() + "\n" +
                "Description: " + indent.getDescription() + "\n\n" +
                "Please log in to the system to review the request.\n\n" +
                "Best regards,\n" +
                "Your Indent Management System";
        emailService.sendEmail("purchaseUser@gmail.com", "Indent Approval Required", emailBody);

        return ResponseEntity.ok(Map.of("message", "Approved and forwarded to Purchase"));
    }


    @PostMapping("/finance/reject")
    public ResponseEntity<?> rejectIndentAsFinance(@RequestBody Map<String, Object> request,
                                                   Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }


        Long indentId = Long.valueOf(request.get("indentId").toString());
        String remark = (String) request.get("remark");

        System.err.println("remark:------------" + remark);
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_FINANCE) {
            return ResponseEntity.badRequest().body("Indent not in Finance stage");
        }

        indent.setRemarkByFinance(remark);
        indent.setStatus(IndentStatus.REJECTED_BY_FINANCE);
        indent.setFinanceApprovalDate(LocalDateTime.now());

        indentRequestRepository.save(indent);
        emailrejectedindent("Finance", indent, indentId);



        return ResponseEntity.ok(Map.of("message", "Indent rejected by Finance"));
    }


    @PostMapping("/finance/payment/reject")
    public ResponseEntity<?> rejectPayment(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Unauthorized");

        Long indentId = Long.valueOf(body.get("indentId").toString());
        String paymentNote = (String) body.get("paymentNote");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_FINANCE_PAYMENT) {
            return ResponseEntity.badRequest().body("Not in payment stage");
        }

        indent.setPaymentNote(paymentNote);
        indent.setStatus(IndentStatus.PAYMENT_REJECTED); // <- You must define this in your IndentStatus enum
        indent.setPaymentCreatedAt(LocalDateTime.now());

        indentRequestRepository.save(indent);
        emailrejectedindent("Finance", indent, indentId);
        return ResponseEntity.ok(Map.of("message", "Payment rejected"));
    }


    @PostMapping("/user/inspection")
    public ResponseEntity<?> confirmProductReceived(@RequestBody Map<String, Object> req, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Not authenticated");
        Long indentId = Long.valueOf(req.get("indentId").toString());

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.DELIVERED) {
            return ResponseEntity.badRequest().body("Not in DELIVERED state");
        }

        indent.setStatus(IndentStatus.UNDER_INSPECTION);
        indent.setUserInspectionDate(LocalDateTime.now());
        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Product inspected and confirmed by USER"));
    }

    @PostMapping("/finance/complete-payment")
    public ResponseEntity<?> completePayment(@RequestBody Map<String, Object> req, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Not authenticated");
        Long indentId = Long.valueOf(req.get("indentId").toString());
        String remark = (String) req.get("remark");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.GFR_GENERATED) {
            return ResponseEntity.badRequest().body("GFR not generated yet");
        }

        indent.setFinanceRemark(remark);
        indent.setStatus(IndentStatus.COMPLETED);
        indent.setPaymentCompletedDate(LocalDateTime.now());
        indentRequestRepository.save(indent);

        return ResponseEntity.ok(Map.of("message", "Payment completed, indent marked as successful"));
    }


    @PostMapping("/user/inspect")
    public ResponseEntity<?> inspectReceivedItem(@RequestBody Map<String, Object> request, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Not authenticated");

        Long indentId = Long.parseLong(request.get("indentId").toString());
        String remark = request.get("remark").toString();

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.AWAITING_USER_INSPECTION) {
            return ResponseEntity.badRequest().body("Indent not in inspection stage");
        }

        indent.setUserInspectionRemark(remark);
        indent.setUserInspectionDate(LocalDateTime.now());
        indent.setStatus(IndentStatus.USER_APPROVED);

        indentRequestRepository.save(indent);
        return ResponseEntity.ok(Map.of("message", "User approved item"));
    }
    @GetMapping("/user/pending-inspection")
    public ResponseEntity<?> getPendingInspections(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

//        User user = (User) authentication.getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();  // assuming getUser() returns the User entity
        List<IndentRequest> pending = indentRequestRepository
                .findByRequestedByIdAndStatus((long) user.getId(), IndentStatus.WAITING_FOR_USER_CONFIRMATION);

        return ResponseEntity.ok(pending);
    }
    @PostMapping("/{indentId}/confirm-inspection")
    public ResponseEntity<?> confirmInspection(@PathVariable Long indentId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.WAITING_FOR_USER_CONFIRMATION) {
            return ResponseEntity.badRequest().body("Indent not in inspection stage");
        }

        String remark = requestBody.get("remark");

        indent.setStatus(IndentStatus.PENDING_PURCHASE_GFR);
        indent.setUserInspectionDate(LocalDateTime.now());
        indent.setRemarkByUser(remark);

        // print all these parameter
        System.err.println("Indent ID: " + indentId);
        System.err.println("Status: " + indent.getStatus());
        System.err.println("User Inspection Date: " + indent.getUserInspectionDate());
        System.err.println("Remark: " + remark);
        indentRequestRepository.save(indent);
        // Send email notification to Purchase
        String emailBody = "Hello Purchase Team,\n\n" +
                "The indent request has been confirmed by the user.\n" +
                "Indent ID: " + indentId + "\n" +
                "Project Name: " + indent.getProjectName() + "\n" +
                "Item Name: " + indent.getItemName() + "\n" +
                "Quantity: " + indent.getQuantity() + "\n" +
                "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                "Department: " + indent.getDepartment() + "\n" +
                "Description: " + indent.getDescription() + "\n\n" +
                "Please log in to the system to make a GFR report for the request.\n\n" +
                "Best regards,\n" +
                "Your Indent Management System";
        emailService.sendEmail("Purchase@gmail.com", "Generate GFR Report", emailBody);
        return ResponseEntity.ok(Map.of("message", "Product confirmed as OK"));
    }



    @PostMapping("/purchase/generate-gfr")
    public ResponseEntity<?> generateGFR(@RequestBody Map<String, Object> request, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Not authenticated");

        Long indentId = Long.parseLong(request.get("indentId").toString());
        String gfrDetails = request.get("gfrDetails").toString();

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.USER_APPROVED) {
            return ResponseEntity.badRequest().body("Indent not approved by user");
        }

        indent.setGfrDetails(gfrDetails);
        indent.setGfrGeneratedDate(LocalDateTime.now());
        indent.setStatus(IndentStatus.GFR_GENERATED);

        indentRequestRepository.save(indent);
        return ResponseEntity.ok(Map.of("message", "GFR generated"));
    }

    @PostMapping("/finance/pay")
    public ResponseEntity<?> markPaymentDone(@RequestBody Map<String, Object> request, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Not authenticated");

        Long indentId = Long.parseLong(request.get("indentId").toString());
        String remark = request.get("remark").toString();

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.GFR_GENERATED) {
            return ResponseEntity.badRequest().body("GFR not generated yet");
        }

        indent.setPaymentRemark(remark);
        indent.setPaymentDate(LocalDateTime.now());
        indent.setStatus(IndentStatus.SUCCESS);

        indentRequestRepository.save(indent);
        return ResponseEntity.ok(Map.of("message", "Payment marked, indent complete"));
    }
    @GetMapping("/purchase/awaiting-inspection")
    public ResponseEntity<?> getIndentsForInspection() {
        return ResponseEntity.ok(indentRequestRepository.findByStatus(IndentStatus.AWAITING_USER_INSPECTION));
    }


    @GetMapping("/purchase/gfr/pending")
    public ResponseEntity<?> getIndentsForGFR(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Unauthorized");

        List<IndentRequest> indents = indentRequestRepository.findByStatus(IndentStatus.PENDING_PURCHASE_GFR);
        return ResponseEntity.ok(indents);
    }

    @PostMapping("/purchase/gfr/submit")
    public ResponseEntity<?> submitGFR(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Unauthorized");

        Long indentId = Long.valueOf(body.get("indentId").toString());
        String gfrNote = (String) body.get("gfrNote");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_PURCHASE_GFR) {
            return ResponseEntity.badRequest().body("Not in GFR stage");
        }

        indent.setGfrNote(gfrNote);
        indent.setStatus(IndentStatus.PENDING_FINANCE_PAYMENT);
        indent.setGfrCreatedAt(LocalDateTime.now());

        indentRequestRepository.save(indent);
// send email to finance to complete the payment for indent
        String emailBody = "Hello Finance Team,\n\n" +
                "An indent request has been approved by Purchase and is now pending your payment.\n" +
                "Indent ID: " + indentId + "\n" +
                "Project Name: " + indent.getProjectName() + "\n" +
                "Item Name: " + indent.getItemName() + "\n" +
                "Quantity: " + indent.getQuantity() + "\n" +
                "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                "Department: " + indent.getDepartment() + "\n" +
                "Description: " + indent.getDescription() + "\n\n" +
                "Please log in to the system to complete the payment.\n\n" +
                "Best regards,\n" +
                "Your Indent Management System";
        emailService.sendEmail("financeteam@gmail.com", "Indent Approval Required", emailBody);

        return ResponseEntity.ok(Map.of("message", "GFR submitted successfully"));
    }

    @GetMapping("/finance/payment/pending")
    public ResponseEntity<?> getIndentForPayment(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Unauthorized");

        List<IndentRequest> indents = indentRequestRepository.findByStatus(IndentStatus.PENDING_FINANCE_PAYMENT);
        return ResponseEntity.ok(indents);
    }

    @PostMapping("/finance/payment/submit")
    public ResponseEntity<?> submitPayment(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Unauthorized");

        Long indentId = Long.valueOf(body.get("indentId").toString());
        String paymentNote = (String) body.get("paymentNote");

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        if (indent.getStatus() != IndentStatus.PENDING_FINANCE_PAYMENT) {
            return ResponseEntity.badRequest().body("Not in Payment stage");
        }

        indent.setPaymentNote(paymentNote);
        indent.setStatus(IndentStatus.PAYMENT_COMPLETED);
        indent.setPaymentCreatedAt(LocalDateTime.now());

        indentRequestRepository.save(indent);

        // Send email to user about payment completion
        User user =indent.getRequestedBy();

        if(user!= null && user.getEmail()!= null){
            String userEmail = user.getEmail();
            String Username= user.getUsername();
            String emailBody = "Hello " +Username + ",\n\n" +
                    "The payment for your indent request has been completed.\n" +
                    "Indent ID: " + indentId + "\n" +
                    "Project Name: " + indent.getProjectName() + "\n" +
                    "Item Name: " + indent.getItemName() + "\n" +
                    "Quantity: " + indent.getQuantity() + "\n" +
                    "Per Piece Cost: " + indent.getPerPieceCost() + "\n" +
                    "Department: " + indent.getDepartment() + "\n" +
                    "Description: " + indent.getDescription() + "\n\n" +
                    "Best regards,\n" +
                    "Your Indent Management System";
            emailService.sendEmail(user.getEmail(), " Indent Payment Completed", emailBody);
        }

        return ResponseEntity.ok(Map.of("message", "Payment cleared successfully"));
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllIndentsForUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<IndentRequest> userIndents = indentRequestRepository.findByRequestedBy(user);

        // Create a list to hold enhanced indent data with reviews
        List<Map<String, Object>> enhancedIndents = new ArrayList<>();

        for (IndentRequest indent : userIndents) {
            Map<String, Object> indentData = new HashMap<>();

            // Copy all original indent properties
            indentData.put("id", indent.getId());
            indentData.put("projectName", indent.getProjectName());
            indentData.put("itemName", indent.getItemName());
            indentData.put("department", indent.getDepartment());
            indentData.put("quantity", indent.getQuantity());
            indentData.put("perPieceCost", indent.getPerPieceCost());
            indentData.put("totalCost", indent.getTotalCost());
            indentData.put("description", indent.getDescription());
            indentData.put("status", indent.getStatus());
            indentData.put("createdAt", indent.getCreatedAt());
            indentData.put("updatedAt", indent.getUpdatedAt());

            // Add all remark fields
            indentData.put("remarkByFla", indent.getRemarkByFla());
            indentData.put("remarkBySla", indent.getRemarkBySla());
            indentData.put("remarkByStore", indent.getRemarkByStore());
            indentData.put("remarkByFinance", indent.getRemarkByFinance());
            indentData.put("remarkByPurchase", indent.getRemarkByPurchase());
            indentData.put("remarkByUser", indent.getRemarkByUser());

            // Add all approval dates
            indentData.put("flaApprovalDate", indent.getFlaApprovalDate());
            indentData.put("slaApprovalDate", indent.getSlaApprovalDate());
            indentData.put("storeApprovalDate", indent.getStoreApprovalDate());
            indentData.put("financeApprovalDate", indent.getFinanceApprovalDate());
            indentData.put("purchaseCompletionDate", indent.getPurchaseCompletionDate());
            indentData.put("userInspectionDate", indent.getUserInspectionDate());

            // Add GFR and Payment fields
            indentData.put("gfrNote", indent.getGfrNote());
            indentData.put("gfrCreatedAt", indent.getGfrCreatedAt());
            indentData.put("paymentNote", indent.getPaymentNote());
            indentData.put("paymentCreatedAt", indent.getPaymentCreatedAt());

            // Add user information
            if (indent.getRequestedBy() != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", indent.getRequestedBy().getUsername());
                userData.put("department", indent.getRequestedBy().getDepartment());
                userData.put("email", indent.getRequestedBy().getEmail());
                indentData.put("requestedBy", userData);
            }

            // Fetch and add purchase reviews
            List<PurchaseReview> purchaseReviews = purchaseReviewRepository.findByIndentRequestId(indent.getId());
            List<Map<String, Object>> reviewsData = new ArrayList<>();

            for (PurchaseReview review : purchaseReviews) {
                Map<String, Object> reviewData = new HashMap<>();
                reviewData.put("id", review.getId());
                reviewData.put("reviewer", review.getReviewer());
                reviewData.put("comment", review.getComment());
                reviewData.put("reviewDate", review.getReviewDate());
                reviewsData.add(reviewData);
            }

            indentData.put("purchaseReviews", reviewsData);
            enhancedIndents.add(indentData);
        }

        return ResponseEntity.ok(enhancedIndents);
    }

    @GetMapping("/indents/{id}/track")
    public ResponseEntity<IndentRequest> trackIndent(@PathVariable Long id) {
        return indentRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<IndentRequestDTO> getAllIndents() {
        List<IndentRequest> indents = indentRequestRepository.findAll();
        return indents.stream()
                .map(IndentRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public IndentRequestDTO getIndentById(@PathVariable Long id) {
        IndentRequest indent = indentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Indent not found"));
        return IndentRequestMapper.toDTO(indent);
    }



}
