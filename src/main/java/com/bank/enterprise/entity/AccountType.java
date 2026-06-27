package com.bank.enterprise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Proxy;

@Data
@Entity
@Table(name = "account_types")
@Proxy(lazy = false)
public class AccountType {

    @Id
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "account_type_name", nullable = false, unique = true, length = 50)
    private String accountTypeName;
}
