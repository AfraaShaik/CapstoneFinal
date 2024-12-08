package com.bankingSystem.mapper;

import com.bankingSystem.DTO.AccountDTO;
import com.bankingSystem.entity.Account;

public class AccountMapper {
    public static Account mapToAccount(AccountDTO accountDTO){
        Account account=new Account(
                accountDTO.getId(),
                accountDTO.getUserId(),
                accountDTO.getAccountType(),
                accountDTO.getBalance(),
                accountDTO.getStatus(),
                accountDTO.getCreatedDate()
        );
        return account;
    }

    public static AccountDTO mapToAccountDTO(Account account){
        AccountDTO accountDTO=new AccountDTO(
                account.getId(),
                account.getUserId(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedDate()
        );
        return accountDTO;
    }
}
