package com.bank.enterprise.dto.request;

import com.bank.enterprise.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class AccountRequestDto {

    @NotNull(message = "Account type is required.")
    private Integer accountTypeId;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
    private String pin;
}

