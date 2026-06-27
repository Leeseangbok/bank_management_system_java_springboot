package com.bank.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private int accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false, unique = true, length = 25)
    private String accountNumber;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    @Column(name = "account_status", length = 20)
    private String accountStatus = "Active";

    @Column(length = 10)
    private String currency = "USD";

    @Column(name = "current_balance", precision = 18, scale = 2)
    private BigDecimal currentBalance;

    @Column(name = "available_balance", precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "overdraft_limit", precision = 18, scale = 2)
    private BigDecimal overdraftLimit;

    @CreationTimestamp
    @Column(name = "opening_date", updatable = false)
    private LocalDateTime openingDate;
}
