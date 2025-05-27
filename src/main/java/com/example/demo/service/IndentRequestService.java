

package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.IndentRemarkRepository;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IndentRequestService {

    @Autowired
    private IndentRequestRepository indentRequestRepository;

    @Autowired
    private IndentRemarkRepository indentRemarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;





public IndentRequest createIndentRequest(Long userId, String itemName, int quantity, Long perPieceCost,
                                         String description, String recipientType, Long recipientId,
                                         String projectName, Double totalCost, String purpose,
                                         String department, String specificationModelDetails) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    User recipient = userRepository.findById(recipientId)
            .orElseThrow(() -> new RuntimeException(recipientType + " not found"));

    IndentRequest indentRequest = new IndentRequest();
    indentRequest.setRequestedBy(user);
    indentRequest.setItemName(itemName);
    indentRequest.setQuantity(quantity);
    indentRequest.setPerPieceCost(perPieceCost);
    indentRequest.setDescription(description);
    indentRequest.setProjectName(projectName);
    indentRequest.setTotalCost(totalCost);
    indentRequest.setPurpose(purpose);
    indentRequest.setDepartment(department);
    indentRequest.setSpecificationModelDetails(specificationModelDetails);
    indentRequest.setCreatedAt(new Date());
//    indentRequest.setStatus(IndentStatus.PENDING_FLA);


    if (recipientType.equalsIgnoreCase("FLA")) {
        indentRequest.setFla(recipient);
        indentRequest.setStatus(IndentStatus.PENDING_FLA);
    } else {
        indentRequest.setSla(recipient);
        indentRequest.setStatus(IndentStatus.PENDING_SLA);
    }

    // Save and notify
    IndentRequest savedIndent = indentRequestRepository.save(indentRequest);

    emailService.sendEmail(
            recipient.getEmail(),
            "Indent Approval Request",
            "An indent has been assigned to you for approval."
    );

    return savedIndent;
}

    // FLA approves and assigns SLA
    public IndentRequest assignSLA(Long indentId, Long slaId) {
        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
        User sla = userRepository.findById(slaId).orElseThrow();

        indent.setSla(sla);
        indent.setStatus(IndentStatus.PENDING_SLA);
        IndentRequest updatedIndent = indentRequestRepository.save(indent);


        // Notify SLA
        emailService.sendEmail(sla.getEmail(), "Indent Assigned to You",
                "You have been selected as SLA for Indent #" + indent.getId());

        return updatedIndent;
    }

    // SLA forwards to Store
    public IndentRequest forwardToStore(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
        indent.setStatus(IndentStatus.PENDING_STORE);
        return indentRequestRepository.save(indent);
    }

    // Store forwards to Finance
    public IndentRequest forwardToFinance(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
        indent.setStatus(IndentStatus.FINANCE_REVIEW);
        return indentRequestRepository.save(indent);
    }

    // Finance forwards to Purchase
    public IndentRequest forwardToPurchase(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
        indent.setStatus(IndentStatus.PURCHASE_REVIEW);
        return indentRequestRepository.save(indent);
    }

    // Mark indent as completed
    public IndentRequest completeIndent(Long indentId) {
        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
        indent.setStatus(IndentStatus.COMPLETED);
        return indentRequestRepository.save(indent);
    }

    // Add a remark
//    public IndentRemark addRemark(Long indentId, Long userId, String remarkText) {
//        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
//        User user = userRepository.findById(userId).orElseThrow();
//
//        IndentRemark remark = new IndentRemark();
//        remark.setIndentRequest(indent);
//        remark.setUser(user);
//        remark.setRemark(remarkText);
//        remark.setCreatedAt(new Date());
//
//        return indentRemarkRepository.save(remark);
//    }

    public List<IndentRequest> getIndentsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        return indentRequestRepository.findByRequestedById(userId);
    }

    // Get all indents requested by a user
    public List<IndentRequest> getUserIndents(Long userId) {
        return indentRequestRepository.findByRequestedById(userId);
    }

    // Get all indents assigned to an FLA
    public List<IndentRequest> getFLAIndents(Long flaId) {
        return indentRequestRepository.findByFlaId(flaId);
    }

    // Get all indents assigned to an SLA
    public List<IndentRequest> getSLAIndents(Long slaId) {
        return indentRequestRepository.findBySlaId(slaId);
    }

    // Get all remarks for an indent
    public List<IndentRemark> getIndentRemarks(Long indentId) {
        return indentRemarkRepository.findByIndentRequestId(indentId);
    }
    public Optional<IndentRequest> getIndentById(Long indentId) {
        return indentRequestRepository.findById(indentId);
    }

}
