package com.bankingSystem.mapper;

import com.bankingSystem.DTO.LoanDTO;
import com.bankingSystem.entity.Loan;

public class LoanMapper {
    public static LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setUserId(loan.getUserId());
        dto.setLoanType(loan.getLoanType());
        dto.setAmount(loan.getAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTenure(loan.getTenure());
        dto.setStatus(loan.getStatus());
        dto.setAppliedDate(loan.getAppliedDate());
        dto.setApprovedDate(loan.getApprovedDate());
        dto.setDocumentUrl(loan.getDocumentUrl());
        return dto;
    }
}
