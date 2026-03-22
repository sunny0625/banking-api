package com.banking.banking_api.service;

import com.banking.banking_api.dto.TransferRequest;
import com.banking.banking_api.model.Account;
import com.banking.banking_api.model.Transaction;
import com.banking.banking_api.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    // ── Transfer funds between two accounts ──────────────────────────────
    // @Transactional ensures that if ANYTHING fails mid-transfer (debit
    // succeeds but credit throws), the entire operation rolls back.
    // This is critical for banking — you cannot have money disappear.
    @Transactional
    public Transaction transfer(TransferRequest request) {

        // Validate: cannot transfer to yourself
        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new RuntimeException("Source and destination accounts cannot be the same.");
        }

        // Validate: amount must be positive
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero.");
        }

        // Load both accounts — throws 404 if either doesn't exist
        Account fromAccount = accountService.getAccountByNumber(request.getFromAccount());
        Account toAccount   = accountService.getAccountByNumber(request.getToAccount());

        // Build the transaction record BEFORE attempting debit.
        // We set status to FAILED first — only mark SUCCESS if everything works.
        Transaction transaction = new Transaction();
        transaction.setFromAccount(request.getFromAccount());
        transaction.setToAccount(request.getToAccount());
        transaction.setAmount(request.getAmount());
        transaction.setType("TRANSFER");
        transaction.setTimestamp(LocalDateTime.now());

        try {
            // Debit sender — throws if insufficient funds
            accountService.debit(fromAccount, request.getAmount());

            // Credit receiver
            accountService.credit(toAccount, request.getAmount());

            transaction.setStatus("SUCCESS");
            transaction.setDescription("Transfer from " + request.getFromAccount()
                    + " to " + request.getToAccount());

        } catch (RuntimeException e) {
            // If debit or credit failed, mark transaction FAILED and rethrow.
            // @Transactional will roll back any DB changes made so far.
            transaction.setStatus("FAILED");
            transaction.setDescription("Transfer failed: " + e.getMessage());
            transactionRepository.save(transaction);
            throw e;
        }

        return transactionRepository.save(transaction);
    }

    // ── Get full transaction history for an account ───────────────────────
    // Returns all transactions where the account was either sender or receiver.
    public List<Transaction> getHistory(String accountNumber) {
        // Verify the account exists before querying history
        accountService.getAccountByNumber(accountNumber);
        return transactionRepository
                .findByFromAccountOrToAccountOrderByTimestampDesc(
                        accountNumber, accountNumber);
    }

    // ── Get only SUCCESSFUL transactions for an account ───────────────────
    public List<Transaction> getSuccessfulHistory(String accountNumber) {
        accountService.getAccountByNumber(accountNumber);
        return transactionRepository
                .findByFromAccountOrToAccountAndStatusOrderByTimestampDesc(
                        accountNumber, accountNumber, "SUCCESS");
    }

    // ── Get all transactions (admin use) ──────────────────────────────────
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }
}
