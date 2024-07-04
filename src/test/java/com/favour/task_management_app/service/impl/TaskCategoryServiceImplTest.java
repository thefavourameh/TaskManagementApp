package com.favour.task_management_app.service.impl;

import com.favour.task_management_app.domain.entities.AppUser;
import com.favour.task_management_app.domain.entities.TaskCategory;
import com.favour.task_management_app.infrastructure.exceptions.TaskCategoryNotFoundException;
import com.favour.task_management_app.payload.request.TaskCategoryRequest;
import com.favour.task_management_app.payload.response.TaskCategoryResponse;
import com.favour.task_management_app.repository.TaskCategoryRepository;
import com.favour.task_management_app.repository.TaskRepository;
import com.favour.task_management_app.repository.UserRepository;
import com.favour.task_management_app.utils.TaskCategoryUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TaskCategoryServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskCategoryRepository taskCategoryRepository;

    @InjectMocks
    private TaskCategoryServiceImpl taskCategoryService;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void updateTaskCategoryByUserId_WithValidIdAndRequest_ShouldUpdateTaskCategory() {
        // Arrange
        Long taskCategoryId = 1L;
        TaskCategoryRequest request = new TaskCategoryRequest();
        request.setTitle("New Title");
        request.setDescription("New Description");

        TaskCategory existingTaskCategory = new TaskCategory();
        existingTaskCategory.setTitle("Old Title");
        existingTaskCategory.setDescription("Old Description");

        when(taskCategoryRepository.findById(anyLong())).thenReturn(Optional.of(existingTaskCategory));
        when(taskCategoryRepository.save(existingTaskCategory)).thenReturn(existingTaskCategory);

        // Act
        TaskCategory updatedTaskCategory = taskCategoryService.updateTaskCategoryByUserId(taskCategoryId, request);

        // Assert
        assertEquals(request.getTitle(), updatedTaskCategory.getTitle());
        assertEquals(request.getDescription(), updatedTaskCategory.getDescription());
        verify(taskCategoryRepository, times(1)).findById(taskCategoryId);
        verify(taskCategoryRepository, times(1)).save(existingTaskCategory);
    }

    @Test
    void updateTaskCategoryByUserId_WithInvalidId_ShouldThrowException() {
        // Arrange
        Long taskCategoryId = 1L;
        TaskCategoryRequest request = new TaskCategoryRequest();

        when(taskCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(TaskCategoryNotFoundException.class, () -> taskCategoryService.updateTaskCategoryByUserId(taskCategoryId, request));
        verify(taskCategoryRepository, times(1)).findById(taskCategoryId);
        verify(taskCategoryRepository, never()).save(any());
    }


    @Test
    void testCreateTaskCategory_Success() {
        // Mocking data
        Long userId = 1L;
        TaskCategoryRequest request = new TaskCategoryRequest("Test Title", "Test Description");
        AppUser user = new AppUser();
        user.setId(userId);

        // Mocking behavior
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Method call
        TaskCategoryResponse response = taskCategoryService.createTaskCategory(userId, request);

        // Verifications
        verify(userRepository, times(1)).findById(userId);
        verify(taskCategoryRepository, times(1)).save(any(TaskCategory.class));
        assertNotNull(response);
        assertEquals(TaskCategoryUtils.TASK_LIST_CREATION_SUCCESS_CODE, response.getResponseCode());
        assertEquals(TaskCategoryUtils.TASK_LIST_CREATION_MESSAGE, response.getResponseMessage());
    }

    @Test
    void testGetAllTaskCategory() {
        // Prepare test data
        Long userId = 123L;
        TaskCategory taskCategory1 = new TaskCategory();
        taskCategory1.setId(1L);
        taskCategory1.setTitle("TaskList 1");
        taskCategory1.setDescription("Description 1");


        TaskCategory taskCategory2 = new TaskCategory();
        taskCategory2.setId(2L);
        taskCategory2.setTitle("TaskCategory 2");
        taskCategory2.setDescription("Description 2");

        List<TaskCategory> taskCategories = new ArrayList<>();
        taskCategories.add(taskCategory1);
        taskCategories.add(taskCategory2);

        // Mock behavior of the repository
        when(taskCategoryRepository.findByUserId(userId)).thenReturn(taskCategories);

        // Call the method under test
        List<TaskCategoryResponse> taskCategoryResponses = taskCategoryService.getAllTaskCategory(userId);

        // Verify the result
        assertNotNull(taskCategoryResponses);
        assertEquals(2, taskCategoryResponses.size());
    }

}

