//
//
package com.example.demo.service;
//
//import com.example.demo.model.*;
//import com.example.demo.repository.IndentRemarkRepository;
//import com.example.demo.repository.IndentRequestRepository;
//import com.example.demo.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//import java.util.*;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class IndentRequestService {
//
//    @Autowired
//    private IndentRequestRepository indentRequestRepository;
//
//    @Autowired
//    private IndentRemarkRepository indentRemarkRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//
//    private EmailService emailService;
//
//
//
//    // Add this method to your IndentRequestService class
//
//    // Add this method to your IndentRequestService class
//
//    // Add this method to your IndentRequestService class
//
//    public IndentRequest createIndentRequestWithCategory(Long userId, String itemName, String category,
//                                                         int quantity, Long perPieceCost, String description,
//                                                         String recipientType, Long recipientId, String projectName,
//                                                         Double totalCost, String purpose, String department,
//                                                         String specificationModelDetails, String fileName,
//                                                         String fileUrl, String fileType) {
//
//        User user = userRepository.findById((long) Math.toIntExact(userId))
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        IndentRequest indent = new IndentRequest();
//        indent.setRequestedBy(user);
//        indent.setUser(user);
//        indent.setItemName(itemName);
//        indent.setCategory(category); // New field
//        indent.setQuantity(quantity);
//        indent.setPerPieceCost(perPieceCost);
//        indent.setDescription(description);
//        indent.setProjectName(projectName);
//        indent.setTotalCost(totalCost);
//        indent.setPurpose(purpose);
//        indent.setDepartment(department);
//        indent.setSpecificationModelDetails(specificationModelDetails);
//
//        // Set file information
//        indent.setFileName(fileName);
//        indent.setFileUrl(fileUrl);
//        indent.setFileType(fileType);
//        indent.setFileUploadedAt(LocalDateTime.now());
//
//        indent.setCreatedAt(LocalDateTime.now());
//        indent.setUpdatedAt(new Date());
//        indent.setStatus(IndentStatus.PENDING_FLA);
//        indent.setInwardEntryGenerated(false);
//
//        // Assign FLA and SLA based on recipientType and recipientId
//        if ("FLA".equals(recipientType)) {
//            User fla = userRepository.findById((long) Math.toIntExact(recipientId))
//                    .orElseThrow(() -> new RuntimeException("FLA not found"));
//            indent.setFla(fla);
//            indent.setAssignedTo(fla);
//        } else if ("SLA".equals(recipientType)) {
//            User sla = userRepository.findById((long) Math.toIntExact(recipientId))
//                    .orElseThrow(() -> new RuntimeException("SLA not found"));
//            indent.setSla(sla);
//            indent.setAssignedTo(sla);
//        }
//
//        IndentRequest savedIndent = indentRequestRepository.save(indent);
//
//        // Log the creation
//        System.out.println("Created indent request with ID: " + savedIndent.getId() +
//                ", Category: " + category + ", File: " + fileName);
//
//        return savedIndent;
//    }
//
//    // Method for batch creation with unique batch ID
//    public List<IndentRequest> createMultipleIndentRequests(Long userId, String projectName,
//                                                            String purpose, String department,
//                                                            String recipientType, Long recipientId,
//                                                            List<Map<String, Object>> itemsList) {
//
//        String batchId = "BATCH_" + System.currentTimeMillis(); // Generate unique batch ID
//        List<IndentRequest> createdIndents = new ArrayList<>();
//
//        for (int i = 0; i < itemsList.size(); i++) {
//            Map<String, Object> itemData = itemsList.get(i);
//
//            IndentRequest indent = createIndentRequestWithCategory(
//                    userId,
//                    (String) itemData.get("itemName"),
//                    (String) itemData.get("category"),
//                    (int) itemData.get("quantity"),
//                    Long.valueOf(itemData.get("perPieceCost").toString()),
//                    (String) itemData.get("description"),
//                    recipientType,
//                    recipientId,
//                    projectName,
//                    Double.valueOf(itemData.get("totalCost").toString()),
//                    purpose,
//                    department,
//                    (String) itemData.get("specificationModelDetails"),
//                    (String) itemData.get("fileName"),
//                    (String) itemData.get("fileUrl"),
//                    (String) itemData.get("fileType")
//            );
//
//            // Set batch information
//            indent.setBatchId(batchId);
//            indent.setBatchSequence(i + 1);
//            indent = indentRequestRepository.save(indent);
//
//            createdIndents.add(indent);
//        }
//
//        return createdIndents;
//    }
//
//    // Method to get indents by category for role-based viewing
//    public List<IndentRequest> getIndentsByCategory(String category, String userRole) {
//        // Get base indents by category
//        List<IndentRequest> indents = indentRequestRepository.findByCategory(category);
//
//        // Filter based on user role and status
//        return indents.stream()
//                .filter(indent -> isAccessibleByRole(indent, userRole))
//                .collect(Collectors.toList());
//    }
//
//    // Enhanced method with status filtering
//    public List<IndentRequest> getIndentsByCategoryAndRole(String category, String userRole, Long userId) {
//        List<IndentStatus> accessibleStatuses = getAccessibleStatusesByRole(userRole);
//
//        if (accessibleStatuses.isEmpty()) {
//            return indentRequestRepository.findByCategory(category);
//        }
//
//        return indentRequestRepository.findByCategoryAndStatusIn(category, accessibleStatuses);
//    }
//
//    private List<IndentStatus> getAccessibleStatusesByRole(String userRole) {
//        switch (userRole) {
//            case "FLA":
//                return Arrays.asList(IndentStatus.PENDING_FLA);
//            case "SLA":
//                return Arrays.asList(IndentStatus.PENDING_SLA);
//            case "FINANCE":
//                return Arrays.asList(IndentStatus.PENDING_FINANCE);
//            case "PURCHASE":
//                return Arrays.asList(IndentStatus.PENDING_PURCHASE);
//            case "STORE":
//                return Arrays.asList(IndentStatus.PENDING_STORE);
//            default:
//                return Arrays.asList(); // Return empty list for USER role to show all
//        }
//    }
//
//    private boolean isAccessibleByRole(IndentRequest indent, String userRole) {
//        // Implement role-based access logic
//        switch (userRole) {
//            case "FLA":
//                return indent.getStatus() == IndentStatus.PENDING_FLA;
//            case "SLA":
//                return indent.getStatus() == IndentStatus.PENDING_SLA;
//            case "FINANCE":
//                return indent.getStatus() == IndentStatus.PENDING_FINANCE;
//            case "PURCHASE":
//                return indent.getStatus() == IndentStatus.PENDING_PURCHASE;
//            default:
//                return true;
//        }
//    }
//
//
//
//
//
//public IndentRequest createIndentRequest(Long userId, String itemName, int quantity, Long perPieceCost,
//                                         String description, String recipientType, Long recipientId,
//                                         String projectName, Double totalCost, String purpose,
//                                         String department, String specificationModelDetails) {
//    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//    User recipient = userRepository.findById(recipientId)
//            .orElseThrow(() -> new RuntimeException(recipientType + " not found"));
//
//    IndentRequest indentRequest = new IndentRequest();
//    indentRequest.setRequestedBy(user);
//    indentRequest.setItemName(itemName);
//    indentRequest.setQuantity(quantity);
//    indentRequest.setPerPieceCost(perPieceCost);
//    indentRequest.setDescription(description);
//    indentRequest.setProjectName(projectName);
//    indentRequest.setTotalCost(totalCost);
//    indentRequest.setPurpose(purpose);
//    indentRequest.setDepartment(department);
//    indentRequest.setSpecificationModelDetails(specificationModelDetails);
//    indentRequest.setCreatedAt(LocalDateTime.now());
////    indentRequest.setStatus(IndentStatus.PENDING_FLA);
//
//
//    if (recipientType.equalsIgnoreCase("FLA")) {
//        indentRequest.setFla(recipient);
//        indentRequest.setStatus(IndentStatus.PENDING_FLA);
//    } else {
//        indentRequest.setSla(recipient);
//        indentRequest.setStatus(IndentStatus.PENDING_SLA);
//    }
//
//    // Save and notify
//    IndentRequest savedIndent = indentRequestRepository.save(indentRequest);
//
//    emailService.sendEmail(
//            recipient.getEmail(),
//            "Indent Approval Request",
//            "An indent has been assigned to you for approval."
//    );
//
//    return savedIndent;
//}
//
//    // FLA approves and assigns SLA
//    public IndentRequest assignSLA(Long indentId, Long slaId) {
//        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
//        User sla = userRepository.findById(slaId).orElseThrow();
//
//        indent.setSla(sla);
//        indent.setStatus(IndentStatus.PENDING_SLA);
//        IndentRequest updatedIndent = indentRequestRepository.save(indent);
//
//
//        // Notify SLA
//        emailService.sendEmail(sla.getEmail(), "Indent Assigned to You",
//                "You have been selected as SLA for Indent #" + indent.getId());
//
//        return updatedIndent;
//    }
//
//    // SLA forwards to Store
//    public IndentRequest forwardToStore(Long indentId) {
//        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
//        indent.setStatus(IndentStatus.PENDING_STORE);
//        return indentRequestRepository.save(indent);
//    }
//
//    // Store forwards to Finance
//    public IndentRequest forwardToFinance(Long indentId) {
//        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
//        indent.setStatus(IndentStatus.FINANCE_REVIEW);
//        return indentRequestRepository.save(indent);
//    }
//
//    // Finance forwards to Purchase
//    public IndentRequest forwardToPurchase(Long indentId) {
//        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
//        indent.setStatus(IndentStatus.PURCHASE_REVIEW);
//        return indentRequestRepository.save(indent);
//    }
//
//    // Mark indent as completed
//    public IndentRequest completeIndent(Long indentId) {
//        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
//        indent.setStatus(IndentStatus.COMPLETED);
//        return indentRequestRepository.save(indent);
//    }
//
//    // Add a remark

//import com.example.demo.model.IndentProduct;
//import com.example.demo.model.IndentRequest;
//import com.example.demo.model.ProductStatus;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;

////    public IndentRemark addRemark(Long indentId, Long userId, String remarkText) {
////        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
////        User user = userRepository.findById(userId).orElseThrow();
////
////        IndentRemark remark = new IndentRemark();
////        remark.setIndentRequest(indent);
////        remark.setUser(user);
////        remark.setRemark(remarkText);
////        remark.setCreatedAt(new Date());
////
////        return indentRemarkRepository.save(remark);
////    }
//
//    public List<IndentRequest> getIndentsByUserId(Long userId) {
//        if (!userRepository.existsById(userId)) {
//            throw new RuntimeException("User not found");
//        }
//        return indentRequestRepository.findByRequestedById(userId);
//    }
//
//    // Get all indents requested by a user
//    public List<IndentRequest> getUserIndents(Long userId) {
//        return indentRequestRepository.findByRequestedById(userId);
//    }
//
//    // Get all indents assigned to an FLA
//    public List<IndentRequest> getFLAIndents(Long flaId) {
//        return indentRequestRepository.findByFlaId(flaId);
//    }
//
//    // Get all indents assigned to an SLA
//    public List<IndentRequest> getSLAIndents(Long slaId) {
//        return indentRequestRepository.findBySlaId(slaId);
//    }
//
//    // Get all remarks for an indent
//    public List<IndentRemark> getIndentRemarks(Long indentId) {
//        return indentRemarkRepository.findByIndentRequestId(indentId);
//    }
//    public Optional<IndentRequest> getIndentById(Long indentId) {
//        return indentRequestRepository.findById(indentId);
//    }
//
//}








import com.example.demo.dto.ProductDTO;
import com.example.demo.model.*;
import com.example.demo.repository.IndentRemarkRepository;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class IndentRequestService {

    @Autowired
    private IndentRequestRepository indentRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Create single indent with multiple products
     */
    public IndentRequest createMultiProductIndent(Long userId,
                                                  String projectName,
                                                  String purpose,
                                                  String department,
                                                  List<ProductDTO> products,
                                                  Long assignedFlaId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User fla = userRepository.findById(assignedFlaId)
                .orElseThrow(() -> new RuntimeException("FLA user not found"));

        // Create main indent request
        IndentRequest indentRequest = new IndentRequest();
        indentRequest.setRequestedBy(user);
        indentRequest.setUser(user);
        indentRequest.setProjectName(projectName);
        indentRequest.setPurpose(purpose);
        indentRequest.setDepartment(department);
        indentRequest.setStatus(IndentStatus.PENDING_FLA);
        indentRequest.setFla(fla);
        indentRequest.setAssignedTo(fla);

        // Create products
        List<IndentProduct> indentProducts = new ArrayList<>();
        int sequence = 1;

        for (ProductDTO productDto : products) {
            IndentProduct product = new IndentProduct();
            product.setItemName(productDto.getItemName());
            product.setCategory(productDto.getCategory());
            product.setQuantity(productDto.getQuantity());
            product.setPerPieceCost(productDto.getPerPieceCost());
            product.setDescription(productDto.getDescription());
            product.setSpecificationModelDetails(productDto.getSpecificationModelDetails());
            product.setProductSequence(sequence++);
            product.setProductStatus(ProductStatus.PENDING);

            // Calculate total cost
            product.calculateTotalCost();

            // File handling
            if (productDto.getFileName() != null && !productDto.getFileName().trim().isEmpty()) {
                product.setFileName(productDto.getFileName());
                product.setFileUrl(productDto.getFileUrl());
                product.setFileType(productDto.getFileType());
                product.setFileSize(productDto.getFileSize());
                product.setFileUploadedAt(LocalDateTime.now());
            }

            // Associate with indent
            product.setIndentRequest(indentRequest);
            indentProducts.add(product);
        }

        indentRequest.setProducts(indentProducts);
        indentRequest.recalculateTotalCost();

        return indentRequestRepository.save(indentRequest);
    }

    /**
     * Get indents assigned to specific role user
     */
    public List<IndentRequest> getIndentsForRole(String username, String role) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        switch (role.toUpperCase()) {
            case "FLA":
                return indentRequestRepository.findByFlaAndStatus(user, IndentStatus.PENDING_FLA);
            case "SLA":
                return indentRequestRepository.findBySlaAndStatus(user, IndentStatus.PENDING_SLA);
//            case "STORE":
//                return indentRequestRepository.findByStoreAndStatus(user, IndentStatus.PENDING_STORE);
//            case "FINANCE":
//                return indentRequestRepository.findByFinanceAndStatus(user, IndentStatus.PENDING_FINANCE);
//            case "PURCHASE":
//                return indentRequestRepository.findByPurchaseAndStatus(user, IndentStatus.PENDING_PURCHASE);
            default:
                return indentRequestRepository.findByRequestedBy(user);
        }
    }

    /**
     * Process product-level approval/rejection
     */
    public void processProductAction(Long indentId, Long productId, String action,
                                     String role, String remarks,
                                     Integer newQuantity, Long newPerPieceCost) {

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        IndentProduct product = indent.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));

        switch (action.toUpperCase()) {
            case "APPROVE":
                if (newQuantity != null || newPerPieceCost != null) {
                    product.applyModification(newQuantity, newPerPieceCost, role, remarks);
                } else {
                    product.approve(role, remarks);
                }
                break;
            case "REJECT":
                product.reject(role, remarks);
                break;
            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }

        // Recalculate indent total
        indent.recalculateTotalCost();
        indentRequestRepository.save(indent);
    }

    /**
     * Check if indent can advance to next stage
     */
    public boolean canAdvanceToNextStage(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        // Must have at least one approved product and no pending products
        boolean hasApprovedProducts = !indent.getApprovedProducts().isEmpty();
        boolean hasPendingProducts = indent.getProducts().stream()
                .anyMatch(p -> p.getProductStatus() == ProductStatus.PENDING);

        return hasApprovedProducts && !hasPendingProducts;
    }

    /**
     * Get indent statistics for dashboard
     */
    public Map<String, Object> getIndentStatistics(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", indent.getProductCount());
        stats.put("approvedProducts", indent.getApprovedProducts().size());
        stats.put("rejectedProducts", (int) indent.getProducts().stream()
                .filter(p -> p.getProductStatus() == ProductStatus.REJECTED).count());
        stats.put("pendingProducts", (int) indent.getProducts().stream()
                .filter(p -> p.getProductStatus() == ProductStatus.PENDING).count());
        stats.put("modifiedProducts", (int) indent.getProducts().stream()
                .filter(p -> p.getProductStatus() == ProductStatus.MODIFIED).count());
        stats.put("totalCost", indent.getDisplayTotalCost());
        stats.put("approvedCost", indent.getApprovedProducts().stream()
                .mapToDouble(IndentProduct::getTotalCost).sum());

        return stats;
    }

    /**
     * Get products grouped by status
     */
    public Map<ProductStatus, List<IndentProduct>> getProductsByStatus(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        return indent.getProducts().stream()
                .collect(Collectors.groupingBy(IndentProduct::getProductStatus));
    }

    /**
     * Generate indent summary report
     */
    public String generateIndentSummaryReport(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        StringBuilder report = new StringBuilder();
        report.append("INDENT SUMMARY REPORT\n");
        report.append("=====================\n\n");
        report.append("Indent Number: ").append(indent.getIndentNumber()).append("\n");
        report.append("Project: ").append(indent.getProjectName()).append("\n");
        report.append("Department: ").append(indent.getDepartment()).append("\n");
        report.append("Status: ").append(indent.getStatus()).append("\n");
        report.append("Created: ").append(indent.getCreatedAt()).append("\n\n");

        report.append("PRODUCTS BREAKDOWN:\n");
        report.append("------------------\n");

        Map<ProductStatus, List<IndentProduct>> productsByStatus = getProductsByStatus(indentId);

        for (Map.Entry<ProductStatus, List<IndentProduct>> entry : productsByStatus.entrySet()) {
            report.append("\n").append(entry.getKey()).append(" (").append(entry.getValue().size()).append("):\n");
            for (IndentProduct product : entry.getValue()) {
                report.append("  - ").append(product.getProductSummary()).append("\n");
            }
        }

        Map<String, Object> stats = getIndentStatistics(indentId);
        report.append("\nSTATISTICS:\n");
        report.append("-----------\n");
        report.append("Total Products: ").append(stats.get("totalProducts")).append("\n");
        report.append("Approved: ").append(stats.get("approvedProducts")).append("\n");
        report.append("Rejected: ").append(stats.get("rejectedProducts")).append("\n");
        report.append("Pending: ").append(stats.get("pendingProducts")).append("\n");
        report.append("Total Cost: ₹").append(String.format("%.2f", (Double) stats.get("totalCost"))).append("\n");
        report.append("Approved Cost: ₹").append(String.format("%.2f", (Double) stats.get("approvedCost"))).append("\n");

        return report.toString();
    }

}




