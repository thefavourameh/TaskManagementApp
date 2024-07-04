package com.favour.task_management_app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.favour.task_management_app.domain.entities.ErrorDetails;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserResponse {
    private String responseMessage;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public UserResponse(String responseMessage, String firstName, String lastName, String email, String phoneNumber) {
        this.responseMessage = responseMessage;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public UserResponse(String responseMessage, ErrorDetails errorDetails) {
        this.responseMessage = responseMessage;
    }


    public UserResponse(String message) {
        this.responseMessage = message;
    }

}


