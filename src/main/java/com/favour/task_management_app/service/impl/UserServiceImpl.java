package com.favour.task_management_app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.favour.task_management_app.domain.entities.AppUser;
import com.favour.task_management_app.infrastructure.config.JwtAuthenticationFilter;
import com.favour.task_management_app.infrastructure.config.JwtService;
import com.favour.task_management_app.infrastructure.exceptions.InvalidAccessException;
import com.favour.task_management_app.infrastructure.exceptions.PasswordNotFoundException;
import com.favour.task_management_app.infrastructure.exceptions.UserNotFoundException;
import com.favour.task_management_app.payload.request.AuthenticationRequest;
import com.favour.task_management_app.payload.request.RegisterRequest;
import com.favour.task_management_app.payload.request.UpdateUserRequest;
import com.favour.task_management_app.payload.response.AuthenticationResponse;
import com.favour.task_management_app.payload.response.RegisterResponse;
import com.favour.task_management_app.payload.response.UserResponse;
import com.favour.task_management_app.repository.UserRepository;
import com.favour.task_management_app.service.UserService;
import com.favour.task_management_app.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private  final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private HttpServletResponse response;

    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public RegisterResponse register(@Valid RegisterRequest registerRequest) throws JsonProcessingException {

        // Validate email format
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registerRequest.getEmail());
        if (!matcher.matches()) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.INVALID_EMAIL_FORMAT_CODE)
                    .responseMessage(UserUtils.INVALID_EMAIL_FORMAT_MESSAGE)
                    .email(registerRequest.getEmail())
                    .build();
        }

        // Validate email domain
        String[] emailParts = registerRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2) {
            System.out.println("Invalid email domain. Email parts: " + Arrays.toString(emailParts));

            return RegisterResponse.builder()
                    .responseCode(UserUtils.INVALID_EMAIL_DOMAIN_CODE)
                    .responseMessage(UserUtils.INVALID_EMAIL_DOMAIN_MESSAGE)
                    .build();
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return RegisterResponse.builder()
                    .responseCode(UserUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(UserUtils.ACCOUNT_EXISTS_MESSAGE)
                    .build();
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        AppUser newUser = AppUser.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .phoneNumber(registerRequest.getPhoneNumber())
                .isEnabled(true)
                .build();

        AppUser savedUser = userRepository.save(newUser);

        return RegisterResponse.builder()
                .responseCode(UserUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(UserUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AppUser user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        String jwtToken = jwtService.generateToken(user);
        user.setToken(jwtToken);

        return AuthenticationResponse.builder()
                .id(user.getId())
                .responseCode(UserUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(UserUtils.LOGIN_SUCCESS_MESSAGE)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .accessToken(jwtToken)
                .build();
    }



    @Override
    public UserResponse editUser(Long id, UpdateUserRequest updateUserRequest) {
        AppUser appUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No User associated with " + id));
        appUser.setFirstName(updateUserRequest.getFirstName());
        appUser.setLastName(updateUserRequest.getLastName());
        appUser.setPhoneNumber(updateUserRequest.getPhoneNumber());
        userRepository.save(appUser);
        return UserResponse.builder()
                .responseMessage(UserUtils.USER_UPDATE_MESSAGE)
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .phoneNumber(appUser.getPhoneNumber())
                .build();
    }

    @Override
    public UserResponse viewUser(Long id) {
        AppUser viewUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserResponse.builder()
                .responseMessage(UserUtils.USER_DETAILS_MESSAGE)
                .firstName(viewUser.getFirstName())
                .lastName(viewUser.getLastName())
                .phoneNumber(viewUser.getPhoneNumber())
                .email(viewUser.getEmail())
                .build();
    }

    @Override
    public String resetPassword(String email, String oldPassword, String newPassword) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No User with this email: " + email));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordNotFoundException("Old password does not match the current password!");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "Your Password has been reset successfully, login with the new password ";
        }
    }


    @Override
    public String forgotPassword(String email, String newPassword, String confirmPassword) {
        Optional<AppUser> optionalAppUser = userRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            return "User with the provided email does not exist.";
        }
        AppUser user = optionalAppUser.get();
        if (!newPassword.equals(confirmPassword)) {
            return "New password and confirm password do not match.";
        }
        String encryptedPassword = passwordEncoder.encode(newPassword);
        userRepository.updateUserPassword(user.getEmail(), encryptedPassword);
        return "Password reset successfully. You can now login with your new password.";
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        AppUser user = optionalUser.get();

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    public String logout() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null){
            String email = authentication.getName();
            Optional<AppUser> appUser = userRepository.findByEmail(email);
            if(appUser.isPresent()){
                AppUser existingUser = appUser.get();
                existingUser.setToken(null);
                userRepository.save(existingUser);
                securityContext.setAuthentication(null);
                SecurityContextHolder.clearContext();
                return "logout successfully";
            }else {
                throw new InvalidAccessException("Invalid User");
            }

        }
        throw new InvalidAccessException("Invalid access");
    }
}

