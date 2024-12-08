package com.bankingSystem.mapper;

import com.bankingSystem.DTO.CardDTO;
import com.bankingSystem.entity.Card;

public class CardMapper {
    public static CardDTO toDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setCardId(card.getCardId());
        dto.setUserId(card.getUserId());
        dto.setCardType(card.getCardType());
        dto.setCardNumber(card.getCardNumber());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus());
        return dto;
    }
}
