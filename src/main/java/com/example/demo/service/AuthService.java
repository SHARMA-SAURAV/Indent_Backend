package com.example.demo.service;

//package com.indentmanagement.service;

//import com.indentmanagement.model.RoleType;
//import com.indentmanagement.model.User;
//import com.indentmanagement.repository.UserRepository;
//import com.indentmanagement.security.JwtUtil;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.RoleType;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(String username, String email, String password, String name,String phone, String department, String designation, String employeeId, String sex, Set<RoleType> role) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);


        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setPhone(phone);
        user.setDepartment(department);
        user.setDesignation(designation);
        user.setEmployeeId(employeeId);
        user.setSex(sex.toUpperCase());
//        user.setRoles(Collections.singleton(role));
        user.setRoles(role);
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtil.generateToken(username);
    }

    //Create a method to get usernameByTargetRoles
    public List<String> getUsernamesByRole(RoleType role) {
        return userRepository.findUsernamesByRole(role);
    }
    // AuthService.java
    public List<UserDTO> getUsersByRole(RoleType role) {
        List<User> users = userRepository.findByRolesContaining(role);
        return users.stream()
                .map(user -> new UserDTO((long) user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }
}
