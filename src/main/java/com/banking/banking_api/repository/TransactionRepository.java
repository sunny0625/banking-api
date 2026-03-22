package com.banking.banking_api.repository;

import com.banking.banking_api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccountOrderByTimestampDesc(
            String fromAccount, String toAccount);

    List<Transaction> findByFromAccountOrToAccountAndStatusOrderByTimestampDesc(
            String fromAccount, String toAccount, String status);

    List<Transaction> findAllByOrderByTimestampDesc();
}
