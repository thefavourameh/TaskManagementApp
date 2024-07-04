package com.favour.task_management_app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.favour.task_management_app.domain.enums.PriorityLevel;
import com.favour.task_management_app.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private PriorityLevel priorityLevel;
    private Status status;

}
