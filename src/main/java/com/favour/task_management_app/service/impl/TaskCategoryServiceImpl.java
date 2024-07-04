package com.favour.task_management_app.service.impl;

import com.favour.task_management_app.domain.entities.AppUser;
import com.favour.task_management_app.domain.entities.Task;
import com.favour.task_management_app.domain.entities.TaskCategory;
import com.favour.task_management_app.infrastructure.exceptions.TaskCategoryNotFoundException;
import com.favour.task_management_app.infrastructure.exceptions.UserNotFoundException;
import com.favour.task_management_app.payload.request.TaskCategoryRequest;
import com.favour.task_management_app.payload.response.TaskCategoryResponse;
import com.favour.task_management_app.repository.TaskCategoryRepository;
import com.favour.task_management_app.repository.TaskRepository;
import com.favour.task_management_app.repository.UserRepository;
import com.favour.task_management_app.service.TaskCategoryService;
import com.favour.task_management_app.utils.TaskCategoryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskCategoryServiceImpl implements TaskCategoryService {
    private  final UserRepository userRepository;

    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    @Transactional
    @Override
    public TaskCategory updateTaskCategoryByUserId(Long taskCategoryId, TaskCategoryRequest taskCategoryRequest) {
        TaskCategory taskCategory = taskCategoryRepository.findById(taskCategoryId)
                .orElseThrow(() -> new TaskCategoryNotFoundException("Task Category not found with id " + taskCategoryId));

        if (taskCategoryRequest.getTitle() != null) {
            taskCategory.setTitle(taskCategoryRequest.getTitle());
        }
        if (taskCategoryRequest.getDescription() != null){
            taskCategory.setDescription(taskCategoryRequest.getDescription());
        }

        return taskCategoryRepository.save(taskCategory);
    }

    @Override
    public TaskCategoryResponse createTaskCategory(Long userId, TaskCategoryRequest request) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found with ID : " + userId));

        TaskCategory newTaskCategory = new TaskCategory();
        newTaskCategory.setTitle(request.getTitle());
        newTaskCategory.setDescription(request.getDescription());
        newTaskCategory.setUser(user);

        taskCategoryRepository.save(newTaskCategory);
        return TaskCategoryResponse.builder()
                .responseCode(TaskCategoryUtils.TASK_LIST_CREATION_SUCCESS_CODE)
                .responseMessage(TaskCategoryUtils.TASK_LIST_CREATION_MESSAGE)
                .title(newTaskCategory.getTitle())
                .description(newTaskCategory.getDescription())
                .build();
    }


    @Override
    public TaskCategory deleteTask(long id) {
        Optional<TaskCategory> taskCategoryOptional = taskCategoryRepository.findById(id);
        if (taskCategoryOptional.isPresent()){
            TaskCategory taskCategory = taskCategoryOptional.get();
            List<Task> tasks = taskCategory.getTasks();
            taskRepository.deleteAll(tasks);
            taskCategoryRepository.delete(taskCategory);
            return taskCategory;
        }
        throw new TaskCategoryNotFoundException("Task not found");
    }

    @Override
    public List<TaskCategoryResponse> getAllTaskCategory(Long userID) {
        List<TaskCategory> taskCategories = taskCategoryRepository.findByUserId(userID);
        if (taskCategories.isEmpty()) {
            throw new TaskCategoryNotFoundException("No task categories found for user with ID: " + userID);
        }
        return taskCategories.stream().map((taskCategory) -> mapToResponse(taskCategory)).collect(Collectors.toList());
    }
    @Override
    public void deleteAllTaskCategory(Long userId) {
        List<TaskCategory> taskCategories = taskCategoryRepository.findByUserId(userId);
        List<Task> tasks = taskRepository.findByUserId(userId);
        if (taskCategories.isEmpty()) {
            throw new TaskCategoryNotFoundException("No task categories found for user with ID: " + userId);
        }
        taskRepository.deleteAll(tasks);
        taskCategoryRepository.deleteAll(taskCategories);

    }

    TaskCategoryResponse mapToResponse(TaskCategory task){
        TaskCategoryResponse response = TaskCategoryResponse.builder()
                .id(task.getId())
                .description(task.getDescription())
                .title(task.getTitle())
                .build();
        return response;

    }


}
