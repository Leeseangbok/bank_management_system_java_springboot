package com.bank.enterprise.controller;

import com.bank.enterprise.dto.response.AccountResponseDto;
import com.bank.enterprise.entity.Account;
import com.bank.enterprise.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getMyAccounts() {
        List<AccountResponseDto> accounts = accountService.getMyAccounts();
        return ResponseEntity.ok(accounts);
    }
}
