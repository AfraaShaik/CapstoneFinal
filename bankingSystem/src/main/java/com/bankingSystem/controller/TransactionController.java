package com.bankingSystem.controller;

import com.bankingSystem.DTO.TransactionDTO;
import com.bankingSystem.DTO.TransactionRequest;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.AccountService;
import com.bankingSystem.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")

public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@RequestBody TransactionRequest request, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        // If role is CUSTOMER, ensure that fromAccount belongs to the current user
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            // Check if fromAccount belongs to the user
            var fromAcc = accountService.getAccountById(request.getFromAccount());
            if (!fromAcc.getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).build();
            }
        }

        // Perform the transaction
        TransactionDTO result = transactionService.performTransfer(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(
            @PathVariable Long accountId,
            Authentication auth
    ) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        // If the user is a CUSTOMER, ensure the account belongs to them
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            var account = accountService.getAccountById(accountId);
            if (!account.getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).build();
            }
        }
        // If ADMIN, no ownership check required (assuming our business logic allows admins to view any account)

        List<TransactionDTO> history = transactionService.getTransactionsForAccount(accountId);
        return ResponseEntity.ok(history);
    }
}
