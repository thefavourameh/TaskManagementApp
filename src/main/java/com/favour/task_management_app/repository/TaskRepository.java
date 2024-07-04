package com.favour.task_management_app.repository;

import com.favour.task_management_app.domain.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTaskCategoryId(Long taskCategoryId);
    List<Task> findByUserId(Long userId);
}
