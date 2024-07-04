package com.favour.task_management_app.infrastructure.exceptions;

public class TaskCategoryNotFoundException extends RuntimeException{
    public TaskCategoryNotFoundException(String message) {
        super(message);
    }
}
