package com.bank.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_id")
    private int credentialId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String salt = "BCRYPT_BUILT_IN";

    @Column(name = "account_locked")
    private boolean accountLocked = false;
}
