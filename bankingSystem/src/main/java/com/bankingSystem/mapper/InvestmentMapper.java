package com.bankingSystem.mapper;

import com.bankingSystem.DTO.InvestmentDTO;
import com.bankingSystem.entity.Investment;

public class InvestmentMapper {
    public static InvestmentDTO toDTO(Investment inv) {
        InvestmentDTO dto = new InvestmentDTO();
        dto.setInvestmentId(inv.getInvestmentId());
        dto.setUserId(inv.getUserId());
        dto.setInvestmentType(inv.getInvestmentType());
        dto.setAmount(inv.getAmount());
        dto.setStartDate(inv.getStartDate());
        dto.setMaturityDate(inv.getMaturityDate());
        dto.setStatus(inv.getStatus());
        return dto;
    }
}
