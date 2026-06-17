package com.bank.enterprise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private String message;
}
