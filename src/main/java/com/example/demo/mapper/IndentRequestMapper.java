package com.example.demo.mapper;

import com.example.demo.dto.IndentRequestDTO;
import com.example.demo.dto.RemarkDTO;
import com.example.demo.model.IndentRequest;
import com.example.demo.model.Remark;

import java.util.List;
import java.util.stream.Collectors;

public class IndentRequestMapper {

    public static IndentRequestDTO toDTO(IndentRequest indent) {
        IndentRequestDTO dto = new IndentRequestDTO();

        dto.setId(indent.getId());
        dto.setItemName(indent.getItemName());
        dto.setQuantity(indent.getQuantity());
        dto.setDescription(indent.getDescription());
        dto.setPerPieceCost(indent.getPerPieceCost());
        dto.setStatus(indent.getStatus().name());

        dto.setRequestedByName(indent.getRequestedBy() != null ? indent.getRequestedBy().getFullName() : null);
        dto.setFlaName(indent.getFla() != null ? indent.getFla().getFullName() : null);
        dto.setSlaName(indent.getSla() != null ? indent.getSla().getFullName() : null);
        dto.setStoreName(indent.getStore() != null ? indent.getStore().getFullName() : null);

        dto.setRemarkByFinance(indent.getRemarkByFinance());
        dto.setFinanceApprovalDate(indent.getFinanceApprovalDate());
        dto.setRemarkByPurchase(indent.getRemarkByPurchase());
        dto.setPurchaseCompletionDate(indent.getPurchaseCompletionDate());

        dto.setGfrGeneratedDate(indent.getGfrGeneratedDate());
        dto.setGfrNote(indent.getGfrNote());
        dto.setGfrDetails(indent.getGfrDetails());

        dto.setFinanceRemark(indent.getFinanceRemark());
        dto.setPaymentCompletedDate(indent.getPaymentCompletedDate());
        dto.setPaymentNote(indent.getPaymentNote());
        dto.setPaymentCreatedAt(indent.getPaymentCreatedAt());

        dto.setUserInspectionDate(indent.getUserInspectionDate());
        dto.setUserInspectionRemark(indent.getUserInspectionRemark());

        dto.setRemarkByStore(indent.getRemarkByStore());
        dto.setStoreApprovalDate(indent.getStoreApprovalDate());

        dto.setFlaApprovalDate(indent.getFlaApprovalDate());
        dto.setRemarkByFla(indent.getRemarkByFla());
        dto.setSlaApprovalDate(indent.getSlaApprovalDate());
        dto.setRemarkBySla(indent.getRemarkBySla());

        dto.setCreatedAt(indent.getCreatedAt());
        dto.setUpdatedAt(indent.getUpdatedAt());

        // Remarks
        List<RemarkDTO> remarkDTOs = indent.getRemarks().stream().map(r -> {
            RemarkDTO rd = new RemarkDTO();
            rd.setRole(r.getRole());
            rd.setMessage(r.getMessage());
            rd.setCreatedAt(r.getCreatedAt());
            return rd;
        }).collect(Collectors.toList());
        dto.setRemarks(remarkDTOs);

        return dto;
    }
}
