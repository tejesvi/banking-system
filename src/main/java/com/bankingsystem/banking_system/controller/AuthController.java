package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.repository.UserRepository;
import com.bankingsystem.banking_system.dto.UserProfileResponse;
import com.bankingsystem.banking_system.util.JwtUtil;
import com.bankingsystem.banking_system.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        user.setCreatedTimestamp(System.currentTimeMillis());

        userRepository.save(user);

        return "User registered";
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public String login(@RequestBody User user) {

        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        
        String currentUsername = SecurityUtil.getCurrentUsername();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        
        String currentUsername = SecurityUtil.getCurrentUsername();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
        
        return ResponseEntity.ok(profile);
    }

    // ...existing code...
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;

        public PasswordChangeRequest() {}

        public PasswordChangeRequest(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}