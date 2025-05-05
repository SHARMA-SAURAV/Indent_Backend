//package service;
//
////package com.villysiu.service;
////
////import com.villysiu.model.Role;
////import com.villysiu.model.User;
////import com.villysiu.repository.UserRepository;
////import com.villysiu.security.JwtUtil;
//import Model.Role;
//import Model.User;
//import Security.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import repository.UserRepository;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//    private final AuthenticationManager authenticationManager;
//
//    public String register(String username, String email, String password, Role role) {
//        User user = User.builder()
//                .username(username)
//                .email(email)
//                .password(passwordEncoder.encode(password))
//                .role(role)
//                .build();
//        userRepository.save(user);
//        return jwtUtil.generateToken(user);
//    }
//
//    public String authenticate(String username, String password) {
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        User user = userRepository.findByUsername(username).orElseThrow();
//        return jwtUtil.generateToken(user);
//    }
//}
