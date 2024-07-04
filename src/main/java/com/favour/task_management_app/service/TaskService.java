package com.favour.task_management_app.service;

import com.favour.task_management_app.domain.entities.Task;
import com.favour.task_management_app.payload.request.TaskRequest;
import com.favour.task_management_app.payload.response.TaskResponse;

import java.util.List;

public interface TaskService {
    public void deleteTask(Long id);
    public void updateTask(Long taskId, TaskRequest updateRequest);
    public TaskRequest createTask(Long userId, Long taskCategoryId, TaskRequest createRequest);
    public Task updateTaskStatus(Long taskId, TaskRequest taskStatus);
    public List<TaskResponse> getTasksByTaskCategoryId(Long taskCategoryId);
    public List<TaskResponse> getAllTasks(Long userId);
}

