package com.bank.enterprise.service;

import com.bank.enterprise.dto.request.AccountRequestDto;
import com.bank.enterprise.dto.response.AccountResponseDto;
import com.bank.enterprise.entity.Account;
import com.bank.enterprise.entity.Credential;
import com.bank.enterprise.entity.Customer;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;

    // --- Core Banking Logic: Create an Account ---
    public AccountRequestDto createAccount(AccountRequestDto accountRequestDto) {
        // 1. Identify the logged-in user from the JWT Security Context
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. Fetch the Customer data associated with this secure login
        Credential credential = credentialRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

        Customer owner = credential.getCustomer();
        // 3. Generate a unique 10-digit account number
        String newAccountNumber = generateUniqueAccountNumber();

        // 4. Build and save the new bank account
        Account newAccount = new Account();
        newAccount.setAccountNumber(newAccountNumber);
        newAccount.setAccountType(accountRequestDto.getAccountType());
        newAccount.setCustomer(owner);

        Account savedAccount = accountRepository.save(newAccount);

        return AccountResponseDto.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .accountType(savedAccount.getAccountType())
                .balance(savedAccount.getCurrentBalance())
                .ownerName(owner.getFirstName() + " " + owner.getLastName())
                .build();
    }
}
