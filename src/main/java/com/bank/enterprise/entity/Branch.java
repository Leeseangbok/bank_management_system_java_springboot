package com.bank.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "branches")

public class Branch {

    @Id
    @GeneratedValue
    @Column
    private Integer branchId;

    @Column
    private String branchName;

    @Column
    private String branchCode;

    @Column
    private String address;

    @Column
    private String city;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
