package com.example.crud.controller;

import com.example.crud.dto.LoginRequest;
import com.example.crud.dto.RegisterRequest;
import com.example.crud.dto.TokenResponse;
import com.example.crud.dto.UserDto;
import com.example.crud.entity.User;
import com.example.crud.security.JwtUtils;
import com.example.crud.security.UserDetailsImpl;
import com.example.crud.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDto userDto = userService.getUserByUsername(userDetails.getUsername());
        
        TokenResponse tokenResponse = TokenResponse.builder()
                .token(jwt)
                .type("Bearer")
                .expiresIn(86400000L) // 24 hours
                .user(userDto)
                .build();
        
        return ResponseEntity.ok(tokenResponse);
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();
        
        UserDto createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
}