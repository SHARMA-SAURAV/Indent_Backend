package com.example.demo.model;

public enum IndentStatus {
    PENDING,        // Waiting for FLA approval
    FLA_APPROVED,   // Approved by FLA, waiting for SLA selection
    PENDING_SLA,    // SLA assigned, waiting for approval
    SLA_APPROVED,   // Approved by SLA, sent to Store
    STORE_REVIEW,   // Store reviewed, sent to Finance
    FINANCE_REVIEW, // Finance reviewed, sent to Purchase
    PURCHASE_REVIEW,// Purchase reviewed, indent completed
    COMPLETED,
    FORWARDED_TO_STORE,
    PENDING_FINANCE,
    PENDING_PURCHASE,
    DELIVERED, UNDER_INSPECTION, GFR_GENERATED,WAITING_FOR_USER_CONFIRMATION,
    REJECTED_BY_FLA,
    AWAITING_USER_INSPECTION,
    USER_APPROVED,
    PENDING_USER_INSPECTION,
    PENDING_FINANCE_PAYMENT,
    PENDING_PURCHASE_GFR,
    REJECTED_BY_SLA,
//    GFR_GENERATED,
    PAYMENT_COMPLETED,
    REJECTED_BY_STORE,
    SUCCESS,
//    PAYMENT_COMPLETED,
//    PENDING_PURCHASE,
//    COMPLETED,

    PENDING_STORE, PENDING_FLA, REJECTED        // Rejected at any stage
}
