package com.tripsplit.auth.service;

import com.tripsplit.auth.dto.LoginRequest;
import com.tripsplit.auth.dto.RegisterRequest;
import com.tripsplit.auth.dto.UserResponse;
import com.tripsplit.auth.entity.User;
import com.tripsplit.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        User user = new User(
            request.getName().trim(),
            request.getEmail().trim().toLowerCase(),
            request.getPassword().trim(),
            request.getPhone() != null ? request.getPhone().trim() : null
        );

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        if (!user.getPassword().equals(request.getPassword().trim())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
