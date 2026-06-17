package com.bank.enterprise.repository;

import com.bank.enterprise.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Integer> {

    Optional<Credential> findByUsername(String username);
}