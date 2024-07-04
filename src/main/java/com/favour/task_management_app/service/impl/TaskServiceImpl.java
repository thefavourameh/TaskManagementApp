package com.favour.task_management_app.service.impl;

import com.favour.task_management_app.domain.entities.AppUser;
import com.favour.task_management_app.domain.entities.Task;
import com.favour.task_management_app.domain.entities.TaskCategory;
import com.favour.task_management_app.domain.enums.PriorityLevel;
import com.favour.task_management_app.domain.enums.Status;
import com.favour.task_management_app.infrastructure.exceptions.TaskCategoryNotFoundException;
import com.favour.task_management_app.infrastructure.exceptions.TaskNotFoundException;
import com.favour.task_management_app.payload.request.TaskRequest;
import com.favour.task_management_app.payload.response.TaskResponse;
import com.favour.task_management_app.repository.TaskCategoryRepository;
import com.favour.task_management_app.repository.TaskRepository;
import com.favour.task_management_app.repository.UserRepository;
import com.favour.task_management_app.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final TaskCategoryRepository taskCategoryRepository;

    @Transactional
    @Override
    public void updateTask(Long taskId, TaskRequest updateRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (updateRequest.getTitle() != null) task.setTitle(updateRequest.getTitle());
        if (updateRequest.getDescription() != null) task.setDescription(updateRequest.getDescription());
        if (updateRequest.getDeadline() != null) task.setDeadline(updateRequest.getDeadline());
        if (updateRequest.getPriorityLevel() != null) task.setPriorityLevel(updateRequest.getPriorityLevel());
        if (updateRequest.getStatus() != null) task.setStatus(updateRequest.getStatus());

        taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException("Task Not Found"));

        taskRepository.delete(task);
    }

    @Override
    public TaskRequest createTask(Long userId, Long taskCategoryId, TaskRequest createRequest) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found"));

        TaskCategory taskCategory = taskCategoryRepository.findById(taskCategoryId)
                .orElseGet(() -> taskCategoryRepository.findAll(PageRequest.of(0, 1))
                        .stream().findFirst()
                        .orElseThrow(() -> new TaskCategoryNotFoundException("No task category found")));

        Task task = new Task();
        task.setTitle(createRequest.getTitle());
        task.setDescription(createRequest.getDescription() != null ? createRequest.getDescription() : "");
        task.setDeadline(createRequest.getDeadline() != null ? createRequest.getDeadline() : null);
        task.setPriorityLevel(createRequest.getPriorityLevel() != null ? createRequest.getPriorityLevel() : PriorityLevel.NONE);
        task.setStatus(createRequest.getStatus() != null ? createRequest.getStatus() : Status.PENDING);
        task.setUser(user);
        task.setTaskCategory(taskCategory);

        taskRepository.save(task);

        return createRequest;
    }


    @Transactional
    @Override
    public Task updateTaskStatus(Long taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (taskRequest.getStatus() != null) task.setStatus(taskRequest.getStatus());

        return taskRepository.save(task);
    }

    @Override
    public List<TaskResponse> getTasksByTaskCategoryId(Long taskCategoryId) {
        List<Task> tasks = taskRepository.findByTaskCategoryId(taskCategoryId);
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasks(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse convertToTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriorityLevel(),
                task.getStatus()
        );
    }

}



