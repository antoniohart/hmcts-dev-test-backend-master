package uk.gov.hmcts.reform.dev.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Task API", description = "Operations related to Tasks")
@RestController
@RequestMapping("/tasks")
public class TaskController {


    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get example task", description = "Returns a sample Task object")
    @GetMapping(value = "/get-example-case", produces = "application/json")
    public ResponseEntity<Task> getExampleCase() {
        return ok(new Task(
            1L,
            "ABC12345",
            "Case Title",
            "Case Description",
            Status.COMPLETED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        ));
    }

    // Create
    @Operation(summary = "Create a new task", description = "Creates a task using the provided details",
        responses = {
            @ApiResponse(description = "Task successfully created", responseCode = "201",
                content = @Content(schema = @Schema(implementation = TaskResponse.class)))})
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {

        TaskResponse createdTask = taskService.createTask(request);
        return ResponseEntity.status(201).body(createdTask);
    }

    @Operation(summary = "Get task by ID", description = "Retrieves a task by its unique ID",
        responses = {
            @ApiResponse(description = "Task retrieved successfully", responseCode = "200",
                content = @Content(schema = @Schema(implementation = TaskResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ok(taskService.getAllTasks());
    }

    @Operation(summary = "Update task status", description = "Updates the status of a task by ID")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        System.out.println("DEBUG - received request: id=" + id + " status=" + status);
        return ok(taskService.updateTaskStatus(id, status));
    }

    @Operation(summary = "Delete task by ID", description = "Deletes a task permanently using its ID",
        responses = {@ApiResponse(description = "Task successfully deleted", responseCode = "204")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


}
