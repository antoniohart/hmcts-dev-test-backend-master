package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DemoUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        LocalDate dueDate = LocalDate.of(2025, 10, 31);
        LocalDate date = LocalDate.of(2025, 10, 31);

        task = new Task(
            1L,
            "sfg",
            "Test Task",
            "Create a unit test for the service layer",
            uk.gov.hmcts.reform.dev.models.Status.PENDING,
            dueDate.atStartOfDay(),
            date.atStartOfDay(),
            date.atStartOfDay()
        );

        taskRequest = new TaskRequest();
        taskRequest.setId(1L);
        taskRequest.setTitle("Test Task");
        task.setDescription("Create a unit test for the service layer");
        task.setDueDate(dueDate.atStartOfDay());
        task.setStatus(Status.PENDING);
        task.setCaseNumber("CASE-12345"); // simulate generated case number

    }

    @Test
    void exampleOfTest() {
        assertTrue(System.currentTimeMillis() > 0, "Example of Unit Test");
    }


    @Test
    void createTask_shouldSaveTaskAndReturnResponse() {
        final LocalDate dueDate = LocalDate.of(2025, 10, 31);

        // 2. Mock Behavior
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // 3. Act (Execute the method under test)
        TaskResponse response = taskService.createTask(taskRequest);

        // Verify that the repository's save method was called exactly once with a Task object
        verify(taskRepository).save(any(Task.class));



        // Verify the response is not null and contains the expected data
        assertNotNull(response, "The response should not be null.");
        assertEquals(1L, response.getId(), "The returned ID should match the saved ID.");
        assertEquals("Test Task", response.getTitle(), "The title should be mapped correctly.");
        assertEquals("Create a unit test for the service layer", response.getDescription(),
                     "The description should be mapped correctly.");
        assertEquals("CASE-12345", response.getCaseNumber(), "The case number should be mapped correctly.");
        assertEquals(Status.PENDING, response.getStatus(), "The status should be set to PENDING.");
        assertEquals(dueDate.atStartOfDay(), response.getDueDate(), "The due date should be mapped correctly.");



    }

    @Test
    void testGetTaskById_success() {
        Long taskId = 1L;

        // Mock repository
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Call the method
        TaskResponse response = taskService.getTaskById(taskId);

        // Verify repository interaction
        verify(taskRepository, times(1)).findById(taskId);

        // Assertions
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals(Status.PENDING, response.getStatus());
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDateUpdated(LocalDateTime.of(2025, 10, 10, 10, 0));

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDateUpdated(LocalDateTime.of(2025, 10, 12, 10, 0));

        List<Task> tasks = Arrays.asList(task2, task1); // Already sorted descending

        // Mock repository behavior
        when(taskRepository.findAll(Sort.by(Sort.Direction.DESC, "dateUpdated"))).thenReturn(tasks);

        // Call the method
        List<TaskResponse> responses = taskService.getAllTasks();

        // Verify repository interaction
        verify(taskRepository, times(1)).findAll(Sort.by(Sort.Direction.DESC, "dateUpdated"));

        // Assertions
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Task 2", responses.get(0).getTitle()); // First should be latest
        assertEquals("Task 1", responses.get(1).getTitle());
    }


    @Test
    void testUpdateTaskStatus_success() {
        // Mock repository behavior
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call method
        TaskResponse response = taskService.updateTaskStatus(1L, "COMPLETED");

        // Verify repository interactions
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));

        // Assertions
        assertNotNull(response);
        assertEquals(Status.COMPLETED, response.getStatus());
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void testUpdateTaskStatus_taskNotFound() {
        // Mock repository to return empty
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            taskService.updateTaskStatus(999L, "COMPLETED")
        );

        assertEquals("Task not found with id 999", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    void testDeleteTask() {
        Long taskId = 1L;

        // Call the method
        taskService.deleteTask(taskId);

        // Verify repository interaction
        verify(taskRepository, times(1)).deleteById(taskId);
    }


    @Test
    void testDeleteTask_repositoryThrowsException() {
        Long taskId = 999L;

        // Mock repository to throw an exception
        doThrow(new RuntimeException("Database error")).when(taskRepository).deleteById(taskId);

        // Expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            taskService.deleteTask(taskId)
        );

        assertEquals("Database error", exception.getMessage());

        // Verify repository interaction
        verify(taskRepository, times(1)).deleteById(taskId);
    }
}

