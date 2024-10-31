package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import com.example.entity.Account;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.BadRequestException;
import com.example.exception.UnauthorizedLoginException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    AccountRepository accountRepository;
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account addAccount(Account account) throws BadRequestException, DuplicateUsernameException {
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            throw new BadRequestException();
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            throw new BadRequestException();
        }
        if (this.findAccountByUsername(account.getUsername()) != null) {
            throw new DuplicateUsernameException();
        }
        return accountRepository.save(account);
    }

    public Account findAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username);
    }

    public Account verifyAccount(Account account) throws UnauthorizedLoginException {
        Account foundAccount = this.findAccountByUsername(account.getUsername());
        if(foundAccount != null && foundAccount.getPassword().equals(account.getPassword())) {
            return foundAccount;
        }
        throw new UnauthorizedLoginException();
    }

    public Account findAccountById(Integer accountId) {
        Optional<Account> optAccount = accountRepository.findById(accountId);
        if(optAccount.isPresent())
            return optAccount.get();
        return null;
    }


}
