package com.bank.enterprise.repository;

import com.bank.enterprise.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // By extending JpaRepository, Spring automatically generates all the
    // basic SQL commands (save, findById, delete, findAll) for us!
}