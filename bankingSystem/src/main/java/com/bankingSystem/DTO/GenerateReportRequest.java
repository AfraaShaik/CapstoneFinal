package com.bankingSystem.DTO;

public class GenerateReportRequest {
    private String type; // e.g. "TRANSACTION_SUMMARY","LOAN_SUMMARY"

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
