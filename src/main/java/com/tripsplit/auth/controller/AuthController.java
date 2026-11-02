package com.tripsplit.auth.controller;

import com.tripsplit.auth.dto.LoginRequest;
import com.tripsplit.auth.dto.RegisterRequest;
import com.tripsplit.auth.dto.UserResponse;
import com.tripsplit.auth.service.AuthService;
import com.tripsplit.common.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final String SESSION_USER_ID = "LOGGED_IN_USER_ID";
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        UserResponse response = authService.register(request);
        session.setAttribute(SESSION_USER_ID, response.getId());
        return ResponseEntity.ok(ApiResponse.ok("User registered successfully", response));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        UserResponse response = authService.login(request);
        session.setAttribute(SESSION_USER_ID, response.getId());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Logout successful", "Session ended"));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestParam(value = "userId", required = false) Long fallbackUserId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            userId = fallbackUserId != null ? fallbackUserId : 1L; // default to first seeded member if demoing
        }
        UserResponse user = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok("Current user retrieved", user));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = authService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", users));
    }
}
