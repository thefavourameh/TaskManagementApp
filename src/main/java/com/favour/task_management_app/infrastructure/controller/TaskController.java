package com.favour.task_management_app.infrastructure.controller;

import com.favour.task_management_app.domain.entities.Task;
import com.favour.task_management_app.payload.request.TaskRequest;
import com.favour.task_management_app.payload.response.TaskResponse;
import com.favour.task_management_app.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/task")
public class TaskController  {

    private final TaskService taskService;

    @PatchMapping("update/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable Long taskId, @RequestBody TaskRequest updateRequest) {
        taskService.updateTask(taskId, updateRequest);
        return ResponseEntity.ok("Task updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);

        return new ResponseEntity<>("Task deleted Successfully!", HttpStatus.NO_CONTENT);
    }

    @PostMapping("/new_task/{userId}/{taskCategoryId}")
    public ResponseEntity<TaskRequest> createTask(@PathVariable Long userId, @PathVariable Long taskCategoryId, @RequestBody TaskRequest createRequest) {
        TaskRequest createdTask = taskService.createTask(userId, taskCategoryId, createRequest);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PatchMapping("/update-status/{taskId}")
    public ResponseEntity<Task> updateStatus(@PathVariable Long taskId, @RequestBody TaskRequest updateStatus){
        Task updatedTask = taskService.updateTaskStatus(taskId, updateStatus);
        return new ResponseEntity<>(updatedTask, HttpStatus.CREATED);
    }

    @GetMapping("/get_tasks/{taskCategoryId}")
    public ResponseEntity<List<TaskResponse>> getTasksByTaskCategoryId(@PathVariable Long taskCategoryId) {
        List<TaskResponse> taskResponses = taskService.getTasksByTaskCategoryId(taskCategoryId);
        if (taskResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/all-tasks/{userId}")
    public ResponseEntity<List<TaskResponse>> getAllTasks(@PathVariable("userId") Long userId) {
        List<TaskResponse> taskResponses = taskService.getAllTasks(userId);
        if (taskResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(taskResponses);
    }
}

