package com.example.demo.controller;

//package com.indentmanagement.controller;
//
//import com.indentmanagement.model.RoleType;
//import com.indentmanagement.service.AuthService;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.RoleType;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

//    @Autowired
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {

        try{
            String username = (String) request.get("username");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String name= (String) request.get("name");
            String phone= (String) request.get("phone");
            String department= (String) request.get("department");
            String designation= (String) request.get("designation");
            String employeeId = (String) request.get("employeeId");
            String sex= (String) request.get("sex");
//        RoleType role = RoleType.valueOf(request.get("role").toUpperCase());
            // Extract roles from JSON and convert to RoleType enum and in json we send in form of list
            List<String> roleStrings = (List<String>) request.get("roles");
            Set<RoleType> roles = roleStrings.stream().map(role->RoleType.valueOf(role.toUpperCase())).collect(Collectors.toSet());
            String response = authService.registerUser(username, email, password,name, phone, department, designation, employeeId,sex, roles);

            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            String message="User Already Exist!";
            return ResponseEntity.badRequest().body("message: "+message);
        }

    }

    // make a endpoint for logout
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Invalidate the JWT token or perform any other logout logic
        // For example, you can remove the token from the client-side storage
        // or mark it as invalid in your database
        // Here, we are just returning a success message
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Invalidate the authentication
            SecurityContextHolder.clearContext();
        }
        // Optionally, you can also invalidate the JWT token on the server-side
        // by storing it in a blacklist or marking it as expired

        return ResponseEntity.ok("User logged out successfully");
    }



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        //print username and password
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);


        String token = authService.loginUser(username, password);
        System.out.println("Token: " + token);
        return ResponseEntity.ok(Map.of("token", token));
    }

//    @GetMapping("/by-role")
//    public ResponseEntity<?> getUsersByRole(@RequestParam String role) {
//        try {
//            RoleType roleType = RoleType.valueOf(role.toUpperCase());
//            List<String> usernames = authService.getUsernamesByRole(roleType);
//            return ResponseEntity.ok(usernames);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Invalid role: " + role);
//        }
//    }

//    @GetMapping("/users/by-role")
//    public ResponseEntity<List<UserDTO>> getUsersByRole(@RequestParam String role) {
//        try {
//            RoleType roleType = RoleType.valueOf(role.toUpperCase());
//            List<UserDTO> users = authService.getUsersByRole(roleType);
//            return ResponseEntity.ok(users);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    // UserController.java
    @GetMapping("/users/by-role")
    public ResponseEntity<?> getUsersByRole(@RequestParam String role) {
        try {
            RoleType roleType = RoleType.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRolesContaining(roleType);

            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<UserDTO> userDTOs = users.stream()
                    .map(user -> new UserDTO((long) user.getId(), user.getUsername()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


//    @GetMapping("/me")
//    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
//        User user = userRepository.findByUsername(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Make sure to include roles in the response
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", user.getId());
//        response.put("username", user.getUsername());
//        response.put("roles", user.getRoles().stream()
//                .map(role -> role.name())
//                .collect(Collectors.toList()));
//
//        return ResponseEntity.ok(response);
//    }

@GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        //print username
        System.err.println(username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("roles", user.getRoles().stream()
                .map(Enum::name)
                .toList());
        return ResponseEntity.ok(response);
    }
}
