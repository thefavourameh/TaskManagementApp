package com.favour.task_management_app.infrastructure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.favour.task_management_app.infrastructure.config.JwtService;
import com.favour.task_management_app.payload.request.AuthenticationRequest;
import com.favour.task_management_app.payload.request.RegisterRequest;
import com.favour.task_management_app.payload.response.AuthenticationResponse;
import com.favour.task_management_app.payload.response.RegisterResponse;
import com.favour.task_management_app.service.impl.UserServiceImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserServiceImpl userService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated
                                      @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) throws MessagingException, JsonProcessingException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        RegisterResponse authenticationResponse = userService.register(registerRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }


    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, String oldPassword, @RequestHeader String newPassword) {
        return new ResponseEntity<>(userService.resetPassword(email, oldPassword, newPassword), HttpStatus.OK);
    }


    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {

        String username = jwtService.extractUsernameFromToken(refreshTokenHeader);

        UserDetails userDetails = userService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public String logout() {
        return userService.logout();
    }
}


