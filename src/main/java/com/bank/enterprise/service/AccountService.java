package com.bank.enterprise.service;

import com.bank.enterprise.dto.request.AccountRequestDto;
import com.bank.enterprise.dto.response.AccountResponseDto;
import com.bank.enterprise.entity.*;
import com.bank.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository      accountRepository;
    private final CredentialRepository   credentialRepository;
    private final BranchRepository       branchRepository;
    private final AccountTypeRepository  accountTypeRepository;
    private final AccountOwnerRepository accountOwnerRepository;
    private final PasswordEncoder        passwordEncoder;

    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto request) {

        // 1. Identify the logged-in customer
        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Credential credential = credentialRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found."));

        Customer owner = credential.getCustomer();

        // 2. Fetch required relationships
        Branch branch = branchRepository.findById(1)
                .orElseThrow(() -> new RuntimeException(
                        "Branch not found. Please seed HQ into the database."));

        AccountType type = accountTypeRepository.findById(request.getAccountTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "Account type not found. ID=" + request.getAccountTypeId()));

        // 3. Hash the PIN
        String pinHash = passwordEncoder.encode(request.getPin());

        // 4. Generate a unique account number
        String accountNumber = generateUniqueAccountNumber();

        // 5. Persist the account
        Account newAccount = new Account();
        newAccount.setAccountNumber(accountNumber);
        newAccount.setPinHash(pinHash);
        newAccount.setAccountType(type);
        newAccount.setBranch(branch);
        newAccount.setCurrentBalance(BigDecimal.ZERO);
        newAccount.setAvailableBalance(BigDecimal.ZERO);
        newAccount.setOverdraftLimit(BigDecimal.ZERO);
        newAccount.setCurrency("USD");
        newAccount.setAccountStatus("ACTIVE");

        Account savedAccount = accountRepository.save(newAccount);

        // 6. Link the account to the customer as primary owner
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

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getMyAccounts() {

        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Credential credential = credentialRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Integer customerId = credential.getCustomer().getCustomerId();

        return accountOwnerRepository.findByCustomer_CustomerId(customerId).stream()
                .map(mapping -> {
                    Account account = mapping.getAccount();
                    return AccountResponseDto.builder()
                            .accountNumber(account.getAccountNumber())
                            .accountType(account.getAccountType())
                            .balance(account.getCurrentBalance())
                            .ownerName(mapping.getCustomer().getFirstName()
                                    + " " + mapping.getCustomer().getLastName())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            long n = (long) (random.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
            accountNumber = String.valueOf(n);
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }
}