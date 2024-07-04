package com.favour.task_management_app.infrastructure.controller;

import com.favour.task_management_app.payload.request.UpdateUserRequest;
import com.favour.task_management_app.payload.response.UserResponse;
import com.favour.task_management_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/edit-user")
    public ResponseEntity<UserResponse> editUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse updatedUser = userService.editUser(id, updateUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> viewUser (@PathVariable Long id){
        UserResponse appUser = userService.viewUser(id);
        return new ResponseEntity<> (appUser, HttpStatus.OK);
    }


}

