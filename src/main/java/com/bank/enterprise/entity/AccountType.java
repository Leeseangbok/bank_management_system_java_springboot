package com.bank.enterprise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "account_types")
public class AccountType {

    @Id
    @Column(name = "id")
    private Integer typeId;

    @Column(name = "account_type_name", nullable = false, unique = true, length = 50)
    private String accountTypeName;
}
