package com.banking.banking_api.service;

import com.banking.banking_api.dto.AccountRequest;
import com.banking.banking_api.model.Account;
import com.banking.banking_api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // ── Create a new bank account ─────────────────────────────────────────
    // Generates a unique account number using UUID (first 8 chars + prefix).
    // New accounts always start with ZERO balance — never trust client-sent balance.
    public Account createAccount(AccountRequest request) {
        Account account = new Account();
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        account.setOwnerName(request.getOwnerName());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    // ── Get account by account number ────────────────────────────────────
    // Throws RuntimeException if not found — SecurityConfig will catch this
    // and return 404. You can replace with a custom exception later.
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountNumber));
    }

    // ── Get all accounts ─────────────────────────────────────────────────
    // Used by admin endpoints. In production, add pagination here.
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // ── Get balance for an account ───────────────────────────────────────
    public BigDecimal getBalance(String accountNumber) {
        return getAccountByNumber(accountNumber).getBalance();
    }

    // ── Deposit money into an account ────────────────────────────────────
    // Amount must be positive — validate before calling this method.
    public Account deposit(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero.");
        }
        Account account = getAccountByNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    // ── Internal: debit an account (called by TransactionService) ────────
    // Package-private so only TransactionService can call it.
    // Never expose a raw debit endpoint publicly — always go through transfer.
    void debit(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(
                    "Insufficient funds in account: " + account.getAccountNumber());
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    // ── Internal: credit an account (called by TransactionService) ───────
    void credit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
}
