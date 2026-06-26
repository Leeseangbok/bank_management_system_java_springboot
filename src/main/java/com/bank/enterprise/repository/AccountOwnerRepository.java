package com.bank.enterprise.repository;

import com.bank.enterprise.entity.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountOwnerRepository extends JpaRepository<AccountOwner, Integer> {
    List<AccountOwner> findByCustomer_CustomerId(Integer customerId);
}