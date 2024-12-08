package com.bankingSystem.service;

import com.bankingSystem.DTO.AccountDTO;
import com.bankingSystem.entity.Account;
import com.bankingSystem.mapper.AccountMapper;
import com.bankingSystem.repository.AccountRepository;
import com.bankingSystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.bankingSystem.entity.User;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository,  NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public AccountDTO createAccount(AccountDTO accountDTO, Long currentUserId){
        Account account=AccountMapper.mapToAccount(accountDTO);
        Account saved=accountRepository.save(account);
        // Notify user about account creation
        if (notificationService.isNotificationAllowed(currentUserId, "ACCOUNT_ACTIVITY")) {
            notificationService.createNotification(currentUserId, "ACCOUNT_ACTIVITY",
                    "Your new account (ID: " + saved.getId() + ") has been created successfully.");
        }
        return AccountMapper.mapToAccountDTO(saved);
    }

    public AccountDTO getAccountById(Long id){
        Account account = accountRepository.findById(id).orElseThrow(()-> new RuntimeException("Account does not exist"));
        return AccountMapper.mapToAccountDTO(account);
    }

    public AccountDTO deposit(Long id, double amount){
        Account account = accountRepository.findById(id).orElseThrow(()-> new RuntimeException("Account does not exist"));
        account.setBalance(account.getBalance()+amount);
        Account updated = accountRepository.save(account);

        Long userId = account.getUserId();
        if (notificationService.isNotificationAllowed(userId, "ACCOUNT_ACTIVITY")) {
            notificationService.createNotification(userId, "ACCOUNT_ACTIVITY",
                    "A deposit of " + amount + " was made to your account (ID: " + id + "). New balance: " + updated.getBalance());
        }

        // Check for low balance threshold, e.g. < 1000
        if (updated.getBalance() < 1000 && notificationService.isNotificationAllowed(userId, "LOW_BALANCE")) {
            notificationService.createNotification(userId, "LOW_BALANCE",
                    "Your account (ID: " + id + ") balance is low: " + updated.getBalance());
        }

        return AccountMapper.mapToAccountDTO(updated);
    }

    public AccountDTO withdraw(Long id, double amount){
        Account account = accountRepository.findById(id).orElseThrow(()-> new RuntimeException("Account does not exist"));
        if(account.getBalance()<amount){
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance()-amount);
        Account updated = accountRepository.save(account);

        Long userId = account.getUserId();
        if (notificationService.isNotificationAllowed(userId, "ACCOUNT_ACTIVITY")) {
            notificationService.createNotification(userId, "ACCOUNT_ACTIVITY",
                    "A withdrawal of " + amount + " was made from your account (ID: " + id + "). New balance: " + updated.getBalance());
        }

        if (updated.getBalance() < 1000 && notificationService.isNotificationAllowed(userId, "LOW_BALANCE")) {
            notificationService.createNotification(userId, "LOW_BALANCE",
                    "Your account (ID: " + id + ") balance is low: " + updated.getBalance());
        }

        return AccountMapper.mapToAccountDTO(updated);
    }

    public List<AccountDTO> getAllAccounts(){
        return accountRepository.findAll().stream().map(AccountMapper::mapToAccountDTO).collect(Collectors.toList());
    }

    public List<AccountDTO> getAccountsByUserId(Long userId){
        return accountRepository.findByUserId(userId).stream().map(AccountMapper::mapToAccountDTO).collect(Collectors.toList());
    }

    public void deleteAccount(Long id){
        Account account = accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account does not exist"));
        accountRepository.delete(account);
    }

    public Long getUserIdByUsername(String username){
        return userRepository.findByUsername(username).map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
