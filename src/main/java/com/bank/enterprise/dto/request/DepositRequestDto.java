package com.bank.enterprise.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequestDto {

    private String accountNumber;
    private BigDecimal amount;
    private String description;
}
