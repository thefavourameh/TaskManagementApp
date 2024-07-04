package com.favour.task_management_app.domain.entities;

import com.favour.task_management_app.domain.enums.PriorityLevel;
import com.favour.task_management_app.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Task")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Task extends BaseClass{
    private String title;

    private String description;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskCategory taskCategory;
}
