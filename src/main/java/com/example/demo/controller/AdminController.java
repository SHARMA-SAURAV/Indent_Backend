package com.example.demo.controller;

import com.example.demo.model.RoleType;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    // Fetch all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update user role
//    @PutMapping("/users/{id}")
//    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Object> request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setRoles((Set<RoleType>) request.get("role"));
//        userRepository.save(user);
//        return ResponseEntity.ok("User role updated successfully");
//    }

    //Update user role with email verification
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        user.setRoles((Set<RoleType>) request.get("role"));

        String oldRole = user.getRoles().toString();
        Set<RoleType> newRole = (Set<RoleType>) request.get("role");
        user.setRoles(newRole);

        userRepository.save(user);

        // Send email notification
        emailService.sendEmail(user.getEmail(), "Role Updated",
                "Hello " + user.getUsername() + ",\n\nYour role has been changed from " + oldRole + " to " + newRole + ".");

        return ResponseEntity.ok("User role updated and email sent");
    }


    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
