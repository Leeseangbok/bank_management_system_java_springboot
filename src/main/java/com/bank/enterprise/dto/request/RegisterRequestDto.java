package com.bank.enterprise.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequestDto {
    // Identity Data
    private String firstName;
    private String lastName;
    private String nationalId;
    private LocalDate dateOfBirth;

    // Access Data
    private String username;
    private String password;
}