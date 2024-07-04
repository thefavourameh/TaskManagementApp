package com.favour.task_management_app.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.favour.task_management_app.domain.entities.AppUser;
import com.favour.task_management_app.infrastructure.config.JwtAuthenticationFilter;
import com.favour.task_management_app.infrastructure.config.JwtService;
import com.favour.task_management_app.payload.request.AuthenticationRequest;
import com.favour.task_management_app.payload.request.RegisterRequest;
import com.favour.task_management_app.payload.request.UpdateUserRequest;
import com.favour.task_management_app.payload.response.AuthenticationResponse;
import com.favour.task_management_app.payload.response.RegisterResponse;
import com.favour.task_management_app.payload.response.UserResponse;
import com.favour.task_management_app.repository.UserRepository;
import com.favour.task_management_app.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class UserServiceImplTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;
    private AuthenticationRequest authenticationRequest;
    private AppUser appUser;
    private String jwtToken;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }


    @Test
    void testRegister() throws MessagingException, JsonProcessingException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password");
        registerRequest.setPhoneNumber("123456789");

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            return invocation.<AppUser>getArgument(0);
        });

        RegisterResponse response = userService.register(registerRequest);

        assertEquals(UserUtils.ACCOUNT_CREATION_SUCCESS_CODE, response.getResponseCode());
    }


    @Test
    void testViewUser() {
        // Mocking data
        Long userId = 1L;

        AppUser existingUser = new AppUser();
        existingUser.setFirstName("Praise");
        existingUser.setLastName("JohnPual");
        existingUser.setPhoneNumber("08140996323");

        // Mocking repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Testing getUserById method
        UserResponse userResponse = userService.viewUser(userId);

        // Assertions
        verify(userRepository).findById(userId);
        // Add assertions to check userResponse fields against existingUser fields
        // Add more assertions as needed
    }

    @Test
    void testViewUser_UserNotFound() {
        // Mocking data
        Long userId = 1L;

        // Mocking repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Testing getUserById method for user not found scenario
        try {
            userService.viewUser(userId);
            fail("Expected UsernameNotFoundException was not thrown");
        } catch (UsernameNotFoundException e) {
            // UsernameNotFoundException thrown as expected
        }

        // Assertions
        verify(userRepository).findById(userId);
        // Add more assertions as needed
    }


    @Test
    public void testForgotPassword_PasswordResetSuccessfully() {
        String email = "existing@example.com";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";
        AppUser user = new AppUser();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encryptedPassword");

        String result = userService.forgotPassword(email, newPassword, confirmPassword);

        assertEquals("Password reset successfully. You can now login with your new password.", result);
        // Verify that userRepository.updateUserPassword was called with correct arguments
        verify(userRepository).updateUserPassword(eq(email), eq("encryptedPassword"));
    }

    @Test
    public void testForgotPassword_PasswordsDoNotMatch() {
        String email = "existing@example.com";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";
        AppUser user = new AppUser();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String result = userService.forgotPassword(email, newPassword, confirmPassword);

        assertEquals("New password and confirm password do not match.", result);
        // Verify that userRepository.updateUserPassword was not called
        verify(userRepository, never()).updateUserPassword(anyString(), anyString());
    }


    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {

        String email = "test@example.com";
        String password = "password";
        boolean isEnabled = true;
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword(password);
        user.setIsEnabled(isEnabled);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent@example.com"));
    }


    @Test
    public void testLogout() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String userEmail = "test@example.com";
        AppUser appUser = new AppUser();
        appUser.setEmail(userEmail);
        when(authentication.getName()).thenReturn(userEmail);
        Optional<AppUser> optionalAppUser = Optional.of(appUser);
        when(userRepository.findByEmail(userEmail)).thenReturn(optionalAppUser);


        String result = userService.logout();

        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).save(appUser);
        verify(securityContext).setAuthentication(null);
        // verify(SecurityContextHolder.clearContext();).clearContext();

        assertEquals("logout successfully", result);
    }
}


