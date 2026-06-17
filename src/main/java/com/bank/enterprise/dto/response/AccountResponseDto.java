package com.bank.enterprise.dto.response;

import com.bank.enterprise.entity.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponseDto {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String ownerName;
}
