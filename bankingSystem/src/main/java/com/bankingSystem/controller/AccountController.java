package com.bankingSystem.controller;

import com.bankingSystem.DTO.AccountDTO;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService){
        this.accountService=accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> addAccount(@RequestBody AccountDTO accountDTO, Authentication auth){
        com.bankingSystem.entity.User currentUser = (com.bankingSystem.entity.User) auth.getPrincipal();
        String role = currentUser.getRole();

        if("CUSTOMER".equals(role)){
            // customer can only create account for themselves
            accountDTO.setUserId(currentUser.getUserId());
        }
        AccountDTO createdAccount = accountService.createAccount(accountDTO, currentUser.getUserId());

        // ADMIN can create for any user (if needed)
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id, Authentication auth){
        com.bankingSystem.entity.User currentUser = (com.bankingSystem.entity.User) auth.getPrincipal();
        String role = currentUser.getRole();
        AccountDTO account = accountService.getAccountById(id);

        if("CUSTOMER".equals(role) && !account.getUserId().equals(currentUser.getUserId())){
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountDTO> deposit(@PathVariable Long id, @RequestBody Map<String,Double> request, Authentication auth){
        com.bankingSystem.entity.User currentUser = (com.bankingSystem.entity.User) auth.getPrincipal();
        String role = currentUser.getRole();
        AccountDTO account = accountService.getAccountById(id);

        if("CUSTOMER".equals(role) && !account.getUserId().equals(currentUser.getUserId())){
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(accountService.deposit(id, request.get("amount")));
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountDTO> withdraw(@PathVariable Long id, @RequestBody Map<String,Double> request, Authentication auth){
        com.bankingSystem.entity.User currentUser = (com.bankingSystem.entity.User) auth.getPrincipal();
        String role = currentUser.getRole();
        AccountDTO account = accountService.getAccountById(id);

        if("CUSTOMER".equals(role) && !account.getUserId().equals(currentUser.getUserId())){
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(accountService.withdraw(id, request.get("amount")));
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts(Authentication auth){
        com.bankingSystem.entity.User currentUser = (com.bankingSystem.entity.User) auth.getPrincipal();
        String role = currentUser.getRole();

        if("ADMIN".equals(role)){
            return ResponseEntity.ok(accountService.getAllAccounts());
        } else {
            return ResponseEntity.ok(accountService.getAccountsByUserId(currentUser.getUserId()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id, Authentication auth){
        com.bankingSystem.entity.User currentUser = (com.bankingSystem.entity.User) auth.getPrincipal();
        String role = currentUser.getRole();
        AccountDTO account = accountService.getAccountById(id);

        if("CUSTOMER".equals(role) && !account.getUserId().equals(currentUser.getUserId())){
            return ResponseEntity.status(403).build();
        }

        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted Successfully");
    }
}
