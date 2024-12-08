package com.bankingSystem.service;

import com.bankingSystem.DTO.LoanDTO;
import com.bankingSystem.DTO.LoanRequest;
import com.bankingSystem.entity.Loan;
import com.bankingSystem.mapper.LoanMapper;
import com.bankingSystem.repository.LoanRepository;
import com.bankingSystem.repository.UserRepository;
import com.bankingSystem.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // Inject this

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public LoanDTO applyForLoan(Long userId, LoanRequest request) {
        // Optional: perform eligibility checks. For now, we assume anyone can apply.
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setLoanType(request.getLoanType());
        loan.setAmount(request.getAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setTenure(request.getTenure());
        loan.setDocumentUrl(request.getDocumentUrl());
        // Status and appliedDate are set by @PrePersist

        Loan saved = loanRepository.save(loan);
        if (notificationService.isNotificationAllowed(userId, "LOAN_UPDATE")) {
            notificationService.createNotification(userId, "LOAN_UPDATE",
                    "Your loan application for amount " + saved.getAmount() + " is submitted and pending approval.");
        }

        return LoanMapper.toDTO(saved);
    }

    public LoanDTO getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        return LoanMapper.toDTO(loan);
    }

    public List<LoanDTO> getLoansForUser(Long userId) {
        return loanRepository.findByUserId(userId).stream().map(LoanMapper::toDTO).collect(Collectors.toList());
    }

    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream().map(LoanMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public LoanDTO approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

        // Set status to APPROVED and set approvedDate
        loan.setStatus("APPROVED");
        loan.setApprovedDate(LocalDateTime.now());
        Loan updated = loanRepository.save(loan);
        if (notificationService.isNotificationAllowed(updated.getUserId(), "LOAN_UPDATE")) {
            notificationService.createNotification(updated.getUserId(), "LOAN_UPDATE",
                    "Your loan (ID: " + loanId + ") has been approved!");
        }
        return LoanMapper.toDTO(updated);
    }

    @Transactional
    public LoanDTO rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus("REJECTED");
        // approvedDate does not apply here, leave it null or ensure it’s null
        loan.setApprovedDate(null);

        Loan updated = loanRepository.save(loan);
        if (notificationService.isNotificationAllowed(updated.getUserId(), "LOAN_UPDATE")) {
            notificationService.createNotification(updated.getUserId(), "LOAN_UPDATE",
                    "Your loan (ID: " + loanId + ") has been rejected.");
        }
        return LoanMapper.toDTO(updated);
    }

}
