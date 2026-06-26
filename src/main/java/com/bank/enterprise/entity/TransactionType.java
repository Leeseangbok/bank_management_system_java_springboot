package com.bank.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transaction_types")
public class TransactionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "transaction_type_name", unique = true, nullable = false, length = 50)
    private String transactionTypeName;
}
