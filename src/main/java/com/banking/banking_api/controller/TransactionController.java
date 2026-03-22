package com.banking.banking_api.controller;


import com.banking.banking_api.dto.TransferRequest;
import com.banking.banking_api.model.Transaction;
import com.banking.banking_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// FIX: TransactionResponse does not exist in Spring or JJWT.
// The correct return type is ResponseEntity<Transaction> — we return the
// Transaction entity directly. This is the standard pattern in Spring Boot.
// No separate TransactionResponse class is needed.

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Fund transfers and transaction history")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // ── POST /api/transactions/transfer ───────────────────────────────────
    // FIX: Return type was TransactionResponse (doesn't exist) — changed to Transaction.
    // FIX: Was an empty method body — added return statement.
    // @Valid triggers validation on TransferRequest (@NotBlank, @DecimalMin).
    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between two accounts")
    public ResponseEntity<Transaction> transfer(
            @Valid @RequestBody TransferRequest req) {
        Transaction result = transactionService.transfer(req);
        return ResponseEntity.ok(result);
    }

    // ── GET /api/transactions/history/{accountNumber} ─────────────────────
    // FIX: Was an empty method body — added return statement.
    // Returns all transactions (sent + received) sorted newest first.
    @GetMapping("/history/{accountNumber}")
    @Operation(summary = "Get full transaction history for an account")
    public ResponseEntity<List<Transaction>> history(
            @PathVariable String accountNumber) {
        List<Transaction> transactions = transactionService.getHistory(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    // ── GET /api/transactions/history/{accountNumber}/success ─────────────
    // Returns only SUCCESSFUL transactions for an account.
    @GetMapping("/history/{accountNumber}/success")
    @Operation(summary = "Get only successful transactions for an account")
    public ResponseEntity<List<Transaction>> successHistory(
            @PathVariable String accountNumber) {
        List<Transaction> transactions = transactionService.getSuccessfulHistory(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    // ── GET /api/transactions ─────────────────────────────────────────────
    // Admin endpoint — returns all transactions across all accounts.
    @GetMapping
    @Operation(summary = "Get all transactions (admin)")
    public ResponseEntity<List<Transaction>> allTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
