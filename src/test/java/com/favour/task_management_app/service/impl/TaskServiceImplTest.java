package com.favour.task_management_app.service.impl;

import com.favour.task_management_app.domain.entities.AppUser;
import com.favour.task_management_app.domain.entities.Task;
import com.favour.task_management_app.domain.entities.TaskCategory;
import com.favour.task_management_app.domain.enums.PriorityLevel;
import com.favour.task_management_app.domain.enums.Status;
import com.favour.task_management_app.infrastructure.exceptions.TaskNotFoundException;
import com.favour.task_management_app.payload.request.TaskRequest;
import com.favour.task_management_app.payload.response.TaskResponse;
import com.favour.task_management_app.repository.TaskCategoryRepository;
import com.favour.task_management_app.repository.TaskRepository;
import com.favour.task_management_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskCategoryRepository taskCategoryRepository;

    @InjectMocks
    private TaskServiceImpl taskService;


    @Test
    void testCreateTask_SuccessfullyCreatesTask() {
        // Arrange
        Long userId = 1L;
        Long taskCategoryId = 1L;
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("This is a test task");
        createRequest.setDeadline(LocalDateTime.now().plusDays(1));
        createRequest.setPriorityLevel(PriorityLevel.HIGH);
        createRequest.setStatus(Status.PENDING);

        AppUser user = new AppUser();
        user.setId(userId);
        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setId(taskCategoryId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskCategoryRepository.findById(taskCategoryId)).thenReturn(Optional.of(taskCategory));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskRequest result = taskService.createTask(userId, taskCategoryId, createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals("This is a test task", result.getDescription());
        assertNotNull(result.getDeadline());
        assertEquals(PriorityLevel.HIGH, result.getPriorityLevel());
        assertEquals(Status.PENDING, result.getStatus());

        verify(userRepository).findById(userId);
        verify(taskCategoryRepository).findById(taskCategoryId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testCreateTaskWithNonexistentUser() {
        // Arrange
        Long userId = 99L; // Assuming this user ID does not exist
        Long taskListId = 1L;
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("Test Task");

        when(userRepository.findById(userId)).thenThrow(new UsernameNotFoundException("User with ID " + userId + " not found"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> taskService.createTask(userId, taskListId, createRequest));
    }

    @Test
    void updateTask_WhenTaskExists_UpdatesSuccessfully() {
        // Arrange
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setTitle("Old Title");

        TaskRequest request = new TaskRequest();
        request.setTitle("New Title");
        request.setDescription("New Description");
        request.setDeadline(LocalDateTime.now());
        request.setPriorityLevel(PriorityLevel.HIGH);
        request.setStatus(Status.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act
        taskService.updateTask(taskId, request);

        // Assert
        verify(taskRepository).save(existingTask);
        assertEquals("New Title", existingTask.getTitle());
        assertEquals("New Description", existingTask.getDescription());
        assertNotNull(existingTask.getDeadline());
        assertEquals(PriorityLevel.HIGH, existingTask.getPriorityLevel());
        assertEquals(Status.IN_PROGRESS, existingTask.getStatus());
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ThrowsException() {
        // Arrange
        Long taskId = 1L;
        TaskRequest request = new TaskRequest();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, request));
    }

    @Test
    void deleteById_ValidId() {
        // Arrange
        Long taskId = 1L;
        Task task = new Task();
        task.setTitle("Test");
        task.setDescription("Write test");
        task.setStatus(Status.PENDING);
        task.setPriorityLevel(PriorityLevel.HIGH);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteById_InvalidId() {

        Long invalidId = 100L;
        when(taskRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(invalidId));
    }

    @Test
    void deleteById_ExceptionThrown() {

        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenThrow(new TaskNotFoundException("Testing that the right exception is thrown"));

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));
    }


    @Test
    void updateTaskStatus_taskExists_statusUpdated() {
        // Arrange
        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(Status.COMPLETED);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setStatus(Status.PENDING);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act
        taskService.updateTaskStatus(taskId, taskRequest);

        // Assert
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(existingTask);
        assert(existingTask.getStatus()).equals(Status.COMPLETED);
    }

    @Test
    void updateTaskStatus_taskNotFound_throwException() {
        // Arrange
        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(Status.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTaskStatus(taskId, taskRequest);
        });
    }

    @Test
    void getTasksByTaskListId_ShouldReturnListOfTasksResponse() {
        // Arrange
        Long taskCategoryId = 1L;
        Task mockTask1 = new Task();
        Task mockTask2 = new Task();
        List<Task> mockTasks = Arrays.asList(mockTask1, mockTask2);

        when(taskRepository.findByTaskCategoryId(taskCategoryId)).thenReturn(mockTasks);

        // Act
        List<TaskResponse> results = taskService.getTasksByTaskCategoryId(taskCategoryId);

        // Assert
        assertEquals(2, results.size());
        verify(taskRepository).findByTaskCategoryId(taskCategoryId);
        assertEquals(results.get(0).getTitle(), null);
        assertEquals(results.get(1).getTitle(), null);
    }

    @Test
    public void testGetAllTasks() {
        Long userId = 123L;

        List<Task> mockTasks = new ArrayList<>();
        mockTasks.add(Task.builder()
                .title("Task 1")
                .description("Description for Task 1")
                .deadline(LocalDateTime.of(2024, 5, 1, 12, 0))
                .priorityLevel(PriorityLevel.HIGH)
                .status(Status.PENDING)
                .build());
        mockTasks.add(Task.builder()
                .title("Task 2")
                .description("Description for Task 2")
                .deadline(LocalDateTime.of(2024, 5, 2, 15, 30))
                .priorityLevel(PriorityLevel.MEDIUM)
                .status(Status.IN_PROGRESS)
                .build());
        mockTasks.add(Task.builder()
                .title("Task 3")
                .description("Description for Task 3")
                .deadline(LocalDateTime.of(2024, 5, 3, 10, 0))
                .priorityLevel(PriorityLevel.LOW)
                .status(Status.COMPLETED)
                .build());


        when(taskRepository.findByUserId(userId)).thenReturn(mockTasks);

        List<TaskResponse> tasksResponses = taskService.getAllTasks(userId);

        assertEquals(mockTasks.size(), tasksResponses.size());
        for (int i = 0; i < mockTasks.size(); i++) {
            Task mockTask = mockTasks.get(i);
            TaskResponse response = tasksResponses.get(i);

            assertEquals(mockTask.getId(), response.getId());
            assertEquals(mockTask.getTitle(), response.getTitle());
        }
    }


}
