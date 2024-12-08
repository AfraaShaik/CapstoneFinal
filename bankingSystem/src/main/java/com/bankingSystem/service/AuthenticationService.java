package com.bankingSystem.service;

import com.bankingSystem.entity.User;
import com.bankingSystem.repository.UserRepository;
import com.bankingSystem.DTO.RegisterUserDTO;
import com.bankingSystem.DTO.LoginUserDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository,
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.authenticationManager=authenticationManager;
        this.passwordEncoder=passwordEncoder;
    }

    public User signup(RegisterUserDTO input){
        if(userRepository.findByUsername(input.getEmail()).isPresent()){
            throw new RuntimeException("Username already taken");
        }
        User user = new User();
        user.setUsername(input.getEmail());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole("CUSTOMER"); // default role for new signups
        user.setActive(true);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDTO input){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
        );
        return userRepository.findByUsername(input.getEmail())
                .orElseThrow(()->new RuntimeException("User not found after authentication"));
    }
}
