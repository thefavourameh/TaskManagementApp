package com.favour.task_management_app.service;

import com.favour.task_management_app.domain.entities.TaskCategory;
import com.favour.task_management_app.payload.request.TaskCategoryRequest;
import com.favour.task_management_app.payload.response.TaskCategoryResponse;

import java.util.List;

public interface TaskCategoryService {
    public TaskCategory updateTaskCategoryByUserId(Long taskCategoryId, TaskCategoryRequest taskCategoryRequest);
    TaskCategoryResponse createTaskCategory(Long id, TaskCategoryRequest request);
    TaskCategory deleteTask(long Id);
    List<TaskCategoryResponse> getAllTaskCategory(Long userID);
    void deleteAllTaskCategory(Long userId);
}
