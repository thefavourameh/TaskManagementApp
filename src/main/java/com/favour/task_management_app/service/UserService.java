package com.favour.task_management_app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.favour.task_management_app.payload.request.AuthenticationRequest;
import com.favour.task_management_app.payload.request.RegisterRequest;
import com.favour.task_management_app.payload.request.UpdateUserRequest;
import com.favour.task_management_app.payload.response.AuthenticationResponse;
import com.favour.task_management_app.payload.response.RegisterResponse;
import com.favour.task_management_app.payload.response.UserResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface UserService {
    RegisterResponse register(@Valid RegisterRequest registerRequest) throws MessagingException, JsonProcessingException;
    AuthenticationResponse authenticate(AuthenticationRequest request);
    UserResponse editUser(Long id, UpdateUserRequest updateUserRequest);

    UserResponse viewUser(Long id);

    String resetPassword(String email, String oldPassword, String newPassword);

    String forgotPassword(String email, String newPassword, String confirmPassword);
}
