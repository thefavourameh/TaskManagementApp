package com.favour.task_management_app.payload.request;

import com.favour.task_management_app.domain.enums.PriorityLevel;
import com.favour.task_management_app.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private PriorityLevel priorityLevel;
    private Status status;

}
