package com.bank.enterprise.service;

import com.bank.enterprise.entity.Credential;
import com.bank.enterprise.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credential credential = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credential not found"));

        return User.builder()
                .username(credential.getUsername())
                .password(credential.getPasswordHash())
                .roles("USER")
                .build();
    }
}
