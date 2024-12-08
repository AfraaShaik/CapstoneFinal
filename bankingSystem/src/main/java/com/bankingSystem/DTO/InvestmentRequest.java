package com.bankingSystem.DTO;

import java.time.LocalDateTime;

public class InvestmentRequest {
    private String investmentType;
    private double amount;
    private LocalDateTime maturityDate;

    // Getters and Setters

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDateTime maturityDate) {
        this.maturityDate = maturityDate;
    }
}
