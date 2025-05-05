//package com.example.demo.service;
//
////package com.indentmanagement.service;
//
////import com.indentmanagement.model.*;
////import com.indentmanagement.repository.IndentRemarkRepository;
////import com.indentmanagement.repository.IndentRequestRepository;
////import com.indentmanagement.repository.UserRepository;
//import com.example.demo.model.IndentRemark;
//import com.example.demo.model.IndentRequest;
//import com.example.demo.model.IndentStatus;
//import com.example.demo.model.User;
//import com.example.demo.repository.IndentRemarkRepository;
//import com.example.demo.repository.IndentRequestRepository;
//import com.example.demo.repository.UserRepository;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class IndentRequestService {
//
//    private final IndentRequestRepository indentRequestRepository;
//    private final UserRepository userRepository;
//    private final IndentRemarkRepository indentRemarkRepository;
//
//    public IndentRequestService(IndentRequestRepository indentRequestRepository,
//                                UserRepository userRepository, IndentRemarkRepository indentRemarkRepository) {
//        this.indentRequestRepository = indentRequestRepository;
//        this.userRepository = userRepository;
//        this.indentRemarkRepository = indentRemarkRepository;
//    }
//
//    // User creates a new indent request
//    public IndentRequest createIndentRequest(Long userId, String itemName, int quantity, String description, Long flaId) {
//        Optional<User> user = userRepository.findById(userId);
//        Optional<User> fla = userRepository.findById(flaId);
//
//        if (user.isEmpty() || fla.isEmpty()) {
//            throw new RuntimeException("User or FLA not found!");
//        }
//
//        IndentRequest indent = new IndentRequest();
//        indent.setRequestedBy(user.get());
//        indent.setItemName(itemName);
//        indent.setQuantity(quantity);
//        indent.setDescription(description);
//        indent.setFla(fla.get());
//        indent.setStatus(IndentStatus.PENDING);
//
//        return indentRequestRepository.save(indent);
//    }
//
//    // FLA assigns an SLA
//    public IndentRequest assignSLA(Long indentId, Long slaId) {
//        IndentRequest indent = indentRequestRepository.findById(indentId)
//                .orElseThrow(() -> new RuntimeException("Indent not found"));
//
//        User sla = userRepository.findById(slaId)
//                .orElseThrow(() -> new RuntimeException("SLA not found"));
//
//        indent.setSla(sla);
//        indent.setStatus(IndentStatus.FLA_APPROVED);
//
//        return indentRequestRepository.save(indent);
//    }
//
//    // Store, Finance, or Purchase roles add remarks
//    public IndentRemark addRemark(Long indentId, Long userId, String remark) {
//        IndentRequest indent = indentRequestRepository.findById(indentId)
//                .orElseThrow(() -> new RuntimeException("Indent not found"));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        IndentRemark indentRemark = new IndentRemark();
//        indentRemark.setIndentRequest(indent);
//        indentRemark.setUser(user);
//        indentRemark.setRemark(remark);
//
//        return indentRemarkRepository.save(indentRemark);
//    }
//}







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

    // Create an indent request
    public IndentRequest createIndentRequest(Long userId, String itemName, int quantity,Long perPieceCost, String description, Long flaId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found")) ;
        User fla = userRepository.findById(flaId).orElseThrow(()-> new RuntimeException("FLA not found")) ;

        IndentRequest indentRequest = new IndentRequest();
        indentRequest.setRequestedBy(user);
        indentRequest.setItemName(itemName);
        indentRequest.setQuantity(quantity);
        indentRequest.setPerPieceCost(perPieceCost);
        indentRequest.setDescription(description);
        indentRequest.setFla(fla);
        indentRequest.setStatus(IndentStatus.PENDING_FLA);
        indentRequest.setCreatedAt(new Date());

        IndentRequest savedIndent = indentRequestRepository.save(indentRequest);

        // Notify FLA
        emailService.sendEmail(fla.getEmail(), "Indent Approval Request",
                "An indent has been assigned to you for approval.");

        return savedIndent;
    }

    //Approved by FLA
    public IndentRequest approveByFLA(Long indentId, Long slaId, String remark) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        User sla = userRepository.findById(slaId)
                .orElseThrow(() -> new RuntimeException("SLA not found"));

        indent.setSla(sla);
        indent.setStatus(IndentStatus.FLA_APPROVED);

        // Optionally add a remark
        if (remark != null && !remark.isEmpty()) {
            addRemark(indentId, slaId, remark);
        }
        emailService.sendEmail(sla.getEmail(), "Indent Assigned to You",
                "You have been selected as SLA for Indent #" + indent.getId());
        return indentRequestRepository.save(indent);
    }


    // Approve by SLA
    public IndentRequest approveBySLA(Long indentId, String remark) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        indent.setStatus(IndentStatus.SLA_APPROVED);

        // Optionally add a remark
        if (remark != null && !remark.isEmpty()) {
            addRemark(indentId, (long) indent.getSla().getId(), remark);
        }

        return indentRequestRepository.save(indent);
    }

    // Approve by Store
    public IndentRequest approveByStore(Long indentId, String remark) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        indent.setStatus(IndentStatus.FINANCE_REVIEW);

        // Optionally add a remark
        if (remark != null && !remark.isEmpty()) {
            addRemark(indentId, (long) indent.getSla().getId(), remark);
        }

        return indentRequestRepository.save(indent);
    }

    // Approve by Finance
    public IndentRequest approveByFinance(Long indentId, String remark) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        indent.setStatus(IndentStatus.PURCHASE_REVIEW);

        // Optionally add a remark
        if (remark != null && !remark.isEmpty()) {
            addRemark(indentId, (long) indent.getSla().getId(), remark);
        }

        return indentRequestRepository.save(indent);
    }

    // Approve by Purchase
    public IndentRequest approveByPurchase(Long indentId, String remark) {
        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        indent.setStatus(IndentStatus.COMPLETED);

        // Optionally add a remark
        if (remark != null && !remark.isEmpty()) {
            addRemark(indentId, (long) indent.getSla().getId(), remark);
        }

        return indentRequestRepository.save(indent);
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
    public IndentRemark addRemark(Long indentId, Long userId, String remarkText) {
        IndentRequest indent = indentRequestRepository.findById(indentId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        IndentRemark remark = new IndentRemark();
        remark.setIndentRequest(indent);
        remark.setUser(user);
        remark.setRemark(remarkText);
        remark.setCreatedAt(new Date());

        return indentRemarkRepository.save(remark);
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
