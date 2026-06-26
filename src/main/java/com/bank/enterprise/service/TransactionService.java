package com.bank.enterprise.service;

import com.bank.enterprise.dto.request.DepositRequestDto;
import com.bank.enterprise.dto.request.TransferRequestDto;
import com.bank.enterprise.entity.Account;
import com.bank.enterprise.entity.Credential;
import com.bank.enterprise.entity.Transaction;
import com.bank.enterprise.entity.TransactionType;
import com.bank.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final AccountOwnerRepository accountOwnerRepository;

    @Transactional
    public String deposit(DepositRequestDto request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account number not found"));

        TransactionType type = transactionTypeRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("Transaction type not found"));

        BigDecimal balanceBefore = account.getCurrentBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());

        account.setCurrentBalance(balanceAfter);
        account.setAvailableBalance(balanceAfter);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setReferenceNumber("DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        transactionRepository.save(transaction);
        return "Successfully deposited transaction $" + request.getAmount() + " to " + account.getAccountNumber() + ". Ref: " + transaction.getReferenceNumber();
    }

    @Transactional
    public String transfer(TransferRequestDto request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        // 1. Authenticate Sender Identity
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Credential credential = credentialRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Credential not found"));
        Integer loggedInCustomerId = credential.getCustomer().getCustomerId();

        // 2. Fetch Accounts
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account number not found"));
        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account number not found"));

        // 3. Security Check: Verify ownership via the account_owners join table
        boolean isOwner = accountOwnerRepository.findByCustomer_CustomerId(loggedInCustomerId).stream()
                .anyMatch(mapping -> mapping.getAccount().getAccountId() == sourceAccount.getAccountId());
        if (isOwner) {
            throw new RuntimeException("UNAUTHORIZED: You do not own the source account.");
        }

        // 4. Financial Check: Are there sufficient funds?
        if (sourceAccount.getAvailableBalance().compareTo(request.getAmount()) <= 0) {
            throw new RuntimeException("Insufficient funds. Available balance: $" + sourceAccount.getAvailableBalance());
        }
        TransactionType type = transactionTypeRepository.findById(3)
                .orElseThrow(() -> new IllegalArgumentException("Transaction type not found"));

        // 5. Calculate New Balances
        BigDecimal sourceBalanceBefore = sourceAccount.getCurrentBalance();
        BigDecimal sourceBalanceAfter = sourceBalanceBefore.subtract(request.getAmount());

        BigDecimal destinationBalanceBefore = destinationAccount.getCurrentBalance();
        BigDecimal destinationBalanceAfter = destinationBalanceBefore.add(request.getAmount());

        // 6. Update Both Accounts
        sourceAccount.setCurrentBalance(sourceBalanceAfter);
        sourceAccount.setAvailableBalance(sourceBalanceAfter);

        destinationAccount.setCurrentBalance(destinationBalanceAfter);
        destinationAccount.setAvailableBalance(destinationBalanceAfter);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // 7. Record the Audit Trail
        Transaction transaction = new Transaction();
        transaction.setAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTransactionType(type);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setBalanceBefore(sourceBalanceBefore);
        transaction.setBalanceAfter(sourceBalanceAfter);
        transaction.setReferenceNumber("DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        transactionRepository.save(transaction);
        return "Successfully transferred $" + request.getAmount() + " to " + sourceAccount.getAccountNumber() + ". Ref: " + transaction.getReferenceNumber();
    }
}
