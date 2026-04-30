package com.example.insightflowserver.service;

import com.example.insightflowserver.model.AuthResponse;
import com.example.insightflowserver.model.LoginRequest;
import com.example.insightflowserver.model.RegisterRequest;
import com.example.insightflowserver.model.User;
import com.example.insightflowserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService  jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest  registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .createdAt(String.valueOf(LocalDateTime.now()))
                .build();

        User savedUser = userRepository.save(user);

        log.info("New user registered: {}", savedUser.getEmail());

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password!");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        log.info("User logged in: {}", user.getEmail());

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName());
    }
}
