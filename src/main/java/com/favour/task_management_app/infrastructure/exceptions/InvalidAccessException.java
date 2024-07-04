package com.favour.task_management_app.infrastructure.exceptions;

public class InvalidAccessException extends RuntimeException{
    public InvalidAccessException (String message){
        super(message);
    }
}
