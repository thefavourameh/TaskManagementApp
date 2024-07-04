package com.favour.task_management_app.infrastructure.controller;

import com.favour.task_management_app.domain.entities.TaskCategory;
import com.favour.task_management_app.payload.request.TaskCategoryRequest;
import com.favour.task_management_app.payload.response.TaskCategoryResponse;
import com.favour.task_management_app.service.TaskCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/task-category")
public class TaskCategoryController {

    private final TaskCategoryService taskCategoryService;

    @PatchMapping("/update-task-category/{id}")
    public ResponseEntity<TaskCategory> updateTaskCategory(@PathVariable Long id, @RequestBody TaskCategoryRequest taskCategoryRequest) {
        TaskCategory taskCategory = taskCategoryService.updateTaskCategoryByUserId(id, taskCategoryRequest);
        if(taskCategory != null){
            return new ResponseEntity<>(taskCategory, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @PostMapping("/create-task-category/{id}")
    public ResponseEntity<TaskCategoryResponse> createTaskCategory(@PathVariable Long id, @RequestBody TaskCategoryRequest request){
        TaskCategoryResponse createdCategory = taskCategoryService.createTaskCategory(id, request);
        return  new ResponseEntity<>(createdCategory,HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-task-category/{id}")
    public ResponseEntity<TaskCategory> deleteTask(@PathVariable long id){
        TaskCategory deletedTaskCategory = taskCategoryService.deleteTask(id);
        if(deletedTaskCategory != null){
            return new ResponseEntity<>(deletedTaskCategory, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/get-task-category/{id}")
    public ResponseEntity<List<TaskCategoryResponse>> getAllTaskCategory(@PathVariable(value = "id") long userId){
        return new ResponseEntity<>(taskCategoryService.getAllTaskCategory(userId),HttpStatus.OK);
    }

    @DeleteMapping("delete-all-task-category/{userId}")
    ResponseEntity<String> deleteAllTaskCategory(@PathVariable Long userId){
        taskCategoryService.deleteAllTaskCategory(userId);

        return new ResponseEntity<>("Task Categories Deleted Successfully", HttpStatus.OK);
    }

}

