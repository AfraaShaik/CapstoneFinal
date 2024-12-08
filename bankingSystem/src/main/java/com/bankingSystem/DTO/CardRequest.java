package com.bankingSystem.DTO;


public class CardRequest {
    private String cardType; // "DEBIT" or "CREDIT"

    // Getters and Setters
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
