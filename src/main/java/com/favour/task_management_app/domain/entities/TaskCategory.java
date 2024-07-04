package com.favour.task_management_app.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "TaskCategory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TaskCategory extends BaseClass{
    private String title;
    private String description;
    @ManyToOne
    private AppUser user;
    @OneToMany(mappedBy = "taskCategory", cascade = CascadeType.ALL)
    private List<Task> tasks;
}
