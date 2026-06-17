package com.bank.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer customerId;

    @Column
    private String firstName
            ;
    @Column
    private String lastName;

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String gender;

    @Column
    private String nationality;

    @Column
    private String nationalId;

    @Column
    private String occupation;

    @Column
    private String maritalStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
