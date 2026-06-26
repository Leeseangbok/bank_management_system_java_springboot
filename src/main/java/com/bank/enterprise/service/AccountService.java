package com.bank.enterprise.service;

import com.bank.enterprise.dto.request.AccountRequestDto;
import com.bank.enterprise.dto.response.AccountResponseDto;
import com.bank.enterprise.entity.*;
import com.bank.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final BranchRepository branchRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountOwnerRepository accountOwnerRepository;

    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto request) {

        // 1. Identify User
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Credential credential = credentialRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Logged in user not found."));
        Customer owner = credential.getCustomer();

        // 2. Fetch Relationships
        Branch branch = branchRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Branch not found. Please insert HQ into database."));
        AccountType type = accountTypeRepository.findById(request.getAccountType().getTypeId())
                .orElseThrow(() -> new RuntimeException("Account type not found."));

        // 3. Generate Account Number
        String newAccountNumber = generateUniqueAccountNumber();

        // 4. Save the Account
        Account newAccount = new Account();
        newAccount.setAccountNumber(newAccountNumber);
        newAccount.setAccountType(type);
        newAccount.setBranch(branch);
        newAccount.setCurrentBalance(BigDecimal.ZERO);
        newAccount.setAvailableBalance(BigDecimal.ZERO);
        newAccount.setOverdraftLimit(BigDecimal.ZERO);
        newAccount.setCurrency("USD");
        newAccount.setAccountStatus("ACTIVE");
        Account savedAccount = accountRepository.save(newAccount);

        // 5. Link Account to Customer
        AccountOwner accountOwner = new AccountOwner();
        accountOwner.setAccount(savedAccount);
        accountOwner.setCustomer(owner);
        accountOwner.setOwnershipType("PRIMARY");
        accountOwnerRepository.save(accountOwner);

        return AccountResponseDto.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .accountType(savedAccount.getAccountType())
                .balance(savedAccount.getCurrentBalance())
                .ownerName(owner.getFirstName() + " " + owner.getLastName())
                .build();
    }

    public List<AccountResponseDto> getMyAccounts() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Credential credential = credentialRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Integer customerId = credential.getCustomer().getCustomerId();

        return accountOwnerRepository.findByCustomer_CustomerId(customerId).stream()
                .map(mapping -> {
                    Account account = mapping.getAccount();
                    return AccountResponseDto.builder()
                            .accountNumber(account.getAccountNumber())
                            .accountType(account.getAccountType())
                            .balance(account.getCurrentBalance())
                            .ownerName(mapping.getCustomer().getFirstName() + " " + mapping.getCustomer().getLastName())
                            .build();
                }).collect(Collectors.toList());
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        boolean isUnique = false;
        do {
            long generatedNumber = (long) (random.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
            accountNumber = String.valueOf(generatedNumber);
            if (accountRepository.findByAccountNumber(accountNumber).isEmpty()) {
                isUnique = true;
            }
        } while (!isUnique);
        return accountNumber;
    }
}