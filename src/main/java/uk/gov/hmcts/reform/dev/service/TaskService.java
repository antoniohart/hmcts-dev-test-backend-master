package uk.gov.hmcts.reform.dev.service;


import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.util.List;

import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final int NUMBER_LENGTH = 6;
    private static final String DIGITS = "0123456789";

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request) {

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setCaseNumber("CASE-" + generateRandomNumberString());
        task.setDescription(request.getDescription());
        task.setStatus(Status.PENDING);
        task.setDueDate(request.getDueDate());

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);

    }

    // Get by ID
    public TaskResponse getTaskById(Long id) {
        return taskRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }


    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "dateUpdated"))
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public TaskResponse updateTaskStatus(Long id, String status) {

        System.out.println("DEBUG - received request: id=" + id + " status=" + status);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id " + id));

        task.setStatus(Status.valueOf(status));
        // task.setStatus(status);
        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }


    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getCaseNumber(),
            task.getDescription(),
            task.getStatus(),
            task.getDueDate()
        );
    }

    public static String generateRandomNumberString() {
        Random random = new Random();
        StringBuilder numberBuilder = new StringBuilder(NUMBER_LENGTH);

        for (int i = 0; i < NUMBER_LENGTH; i++) {
            // Get a random index between 0 (inclusive) and 9 (inclusive)
            int randomIndex = random.nextInt(DIGITS.length());

            // Append the digit character at the random index
            numberBuilder.append(DIGITS.charAt(randomIndex));
        }

        return numberBuilder.toString();
    }

}
