package com.bank.enterprise.controller;

import com.bank.enterprise.dto.request.AccountRequestDto;
import com.bank.enterprise.dto.response.AccountResponseDto;
import com.bank.enterprise.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getMyAccounts() {
        List<AccountResponseDto> accounts = accountService.getMyAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> openNewAccount(@RequestBody AccountRequestDto request) {
        AccountResponseDto newAccount = accountService.createAccount(request);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }
}