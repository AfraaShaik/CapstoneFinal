package com.bankingSystem.service;

import com.bankingSystem.DTO.TransactionDTO;
import com.bankingSystem.DTO.TransactionRequest;
import com.bankingSystem.entity.Account;
import com.bankingSystem.entity.Transaction;
import com.bankingSystem.mapper.TransactionMapper;
import com.bankingSystem.repository.AccountRepository;
import com.bankingSystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService; // Inject this

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                              NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public TransactionDTO performTransfer(TransactionRequest request) {
        // Validate accounts
        Account fromAcc = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("From Account does not exist"));
        Account toAcc = accountRepository.findById(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("To Account does not exist"));

        // Check account statuses
        if (!"ACTIVE".equalsIgnoreCase(fromAcc.getStatus())) {
            throw new RuntimeException("From Account is not active");
        }
        if (!"ACTIVE".equalsIgnoreCase(toAcc.getStatus())) {
            throw new RuntimeException("To Account is not active");
        }

        // Check sufficient balance
        if (fromAcc.getBalance() < request.getAmount()) {
            // Create failed transaction record
            Transaction failedTx = new Transaction();
            failedTx.setFromAccount(request.getFromAccount());
            failedTx.setToAccount(request.getToAccount());
            failedTx.setAmount(request.getAmount());
            failedTx.setType(request.getType());
            failedTx.setStatus("FAILED");
            transactionRepository.save(failedTx);

            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from fromAccount
        fromAcc.setBalance(fromAcc.getBalance() - request.getAmount());
        accountRepository.save(fromAcc);

        // Add to toAccount
        toAcc.setBalance(toAcc.getBalance() + request.getAmount());
        accountRepository.save(toAcc);

        // Save successful transaction
        Transaction tx = new Transaction();
        tx.setFromAccount(request.getFromAccount());
        tx.setToAccount(request.getToAccount());
        tx.setAmount(request.getAmount());
        tx.setType(request.getType());
        tx.setStatus("SUCCESS");
        tx.setTimestamp(LocalDateTime.now());
        Transaction savedTx = transactionRepository.save(tx);
        if (notificationService.isNotificationAllowed(fromAcc.getUserId(), "FUND_TRANSFER")) {
            notificationService.createNotification(fromAcc.getUserId(), "FUND_TRANSFER",
                    "You transferred " + request.getAmount() + " to account " + request.getToAccount());
        }

        if (notificationService.isNotificationAllowed(toAcc.getUserId(), "FUND_TRANSFER")) {
            notificationService.createNotification(toAcc.getUserId(), "FUND_TRANSFER",
                    "You received " + request.getAmount() + " from account " + request.getFromAccount());
        }
        return TransactionMapper.toDTO(savedTx);
    }
    public List<TransactionDTO> getTransactionsForAccount(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(accountId, accountId);
        return transactions.stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
