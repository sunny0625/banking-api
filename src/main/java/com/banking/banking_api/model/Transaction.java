package com.banking.banking_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromAccount;

    @Column(nullable = false)
    private String toAccount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String type;        // TRANSFER, DEPOSIT
    private String status;      // SUCCESS, FAILED
    private String description;
    private LocalDateTime timestamp;
}
