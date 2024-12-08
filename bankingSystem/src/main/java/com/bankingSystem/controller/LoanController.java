package com.bankingSystem.controller;

import com.bankingSystem.DTO.LoanDTO;
import com.bankingSystem.DTO.LoanRequest;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.LoanService;
import com.bankingSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")

public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    public LoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    @PostMapping("/apply")
    public ResponseEntity<LoanDTO> applyForLoan(@RequestBody LoanRequest request, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }
        // currentUser.getUserId() is the user who is applying
        LoanDTO loan = loanService.applyForLoan(currentUser.getUserId(), request);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long loanId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        LoanDTO loan = loanService.getLoanById(loanId);
        // If CUSTOMER, ensure they own it
        if ("CUSTOMER".equalsIgnoreCase(role) && !loan.getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDTO>> getLoansForUser(@PathVariable Long userId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        // If CUSTOMER tries to see another user's loans, forbid
        if ("CUSTOMER".equalsIgnoreCase(role) && !currentUser.getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        // If ADMIN or USER wants to see their own loans
        List<LoanDTO> loans = loanService.getLoansForUser(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAllLoans(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        List<LoanDTO> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/{loanId}/approve")
    public ResponseEntity<LoanDTO> approveLoan(@PathVariable Long loanId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        LoanDTO approvedLoan = loanService.approveLoan(loanId);
        return ResponseEntity.ok(approvedLoan);
    }
    @PutMapping("/{loanId}/reject")
    public ResponseEntity<LoanDTO> rejectLoan(@PathVariable Long loanId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        LoanDTO rejectedLoan = loanService.rejectLoan(loanId);
        return ResponseEntity.ok(rejectedLoan);
    }

}
