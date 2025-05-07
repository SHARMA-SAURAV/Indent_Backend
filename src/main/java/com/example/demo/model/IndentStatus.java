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
    PENDING_STORE, PENDING_FLA, REJECTED        // Rejected at any stage
}
