package com.bank.enterprise.dto.request;

import com.bank.enterprise.entity.AccountType;
import lombok.Data;

@Data
public class AccountRequestDto {
    private AccountType accountType;
}