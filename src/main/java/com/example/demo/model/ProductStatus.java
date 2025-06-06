package com.example.demo.model;

public enum ProductStatus {
    PENDING,    // Waiting for approval
    APPROVED,   // Approved by current role
    REJECTED,   // Rejected by current role
    MODIFIED ,
    APPROVED_BY_FLA,
    REJECTED_BY_FLA,
    APPROVED_BY_SLA,
    REJECTED_BY_SLA,// Approved with modifications (quantity/cost changes)
    APPROVED_BY_STORE,
    REJECTED_BY_STORE,
    APPROVED_BY_FINANCE,// Approved by Store
    REJECTED_BY_FINANCE,
    APPROVED_BY_PURCHASE,
    REJECTED_BY_PURCHASE,// Rejected by Store
}
