package com.bank.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account_owners")
public class AccountOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Integer mappingId;

    // Links to the Accounts table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Links to the Customers table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Can be PRIMARY, JOINT, etc.
    @Column(name = "ownership_type", length = 20)
    private String ownershipType = "PRIMARY";
}