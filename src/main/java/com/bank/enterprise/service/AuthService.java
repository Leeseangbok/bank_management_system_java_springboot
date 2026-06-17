package com.bank.enterprise.service;

import com.bank.enterprise.dto.request.LoginRequestDto;
import com.bank.enterprise.dto.request.RegisterRequestDto;
import com.bank.enterprise.dto.response.AuthResponseDto;
import com.bank.enterprise.entity.Credential;
import com.bank.enterprise.entity.Customer;
import com.bank.enterprise.repository.CredentialRepository;
import com.bank.enterprise.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public String registerUser(RegisterRequestDto request) {
        // 1. Check if username is taken
        if (credentialRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken.");
        }

        // 2. Build and save the Identity (Customer)
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setNationalId(request.getNationalId());
        customer.setDateOfBirth(request.getDateOfBirth());
        Customer savedCustomer = customerRepository.save(customer);

        // 3. Build, Hash, and save the Access (Credential)
        Credential credential = new Credential();
        credential.setCustomer(savedCustomer);
        credential.setUsername(request.getUsername());
        credential.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        credentialRepository.save(credential);

        return "User registered successfully!";
    }

    public AuthResponseDto loginUser(LoginRequestDto request) {
        Credential credential = credentialRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password."));

        if (credential.isAccountLocked()) {
            throw new RuntimeException("This account has been locked. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), credential.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password.");
        }

        // Generate the token if authentication succeeds
        String token = jwtService.generateToken(credential.getUsername());

        return new AuthResponseDto(token, "Bearer", "Login successful!");
    }
}