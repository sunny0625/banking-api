package com.banking.banking_api.controller;

import com.banking.banking_api.dto.AccountRequest;
import com.banking.banking_api.model.Account;
import com.banking.banking_api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Bank account management")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // ── POST /api/accounts ────────────────────────────────────────────────
    // FIX: Was an empty method body — added return statement.
    // Returns 201 CREATED with the new account object including generated
    // account number and zero balance.
    @PostMapping
    @Operation(summary = "Create a new bank account")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest req) {
        Account created = accountService.createAccount(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── GET /api/accounts/{accountNumber} ─────────────────────────────────
    // FIX: Was an empty method body — added return statement.
    // Returns 200 OK with account details, or 404 if not found (thrown by service).
    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details and current balance")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    // ── GET /api/accounts ─────────────────────────────────────────────────
    // FIX: Was an empty method body — added return statement.
    @GetMapping
    @Operation(summary = "List all accounts")
    public ResponseEntity<List<Account>> listAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // ── GET /api/accounts/{accountNumber}/balance ─────────────────────────
    // Convenience endpoint — returns just the balance as a number.
    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance only")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        BigDecimal balance = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

    // ── POST /api/accounts/{accountNumber}/deposit ────────────────────────
    // Deposit money into an account. Amount passed as a request param.
    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit funds into an account")
    public ResponseEntity<Account> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {
        Account updated = accountService.deposit(accountNumber, amount);
        return ResponseEntity.ok(updated);
    }
}
