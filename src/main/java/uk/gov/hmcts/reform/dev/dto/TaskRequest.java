package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import lombok.Setter;
import uk.gov.hmcts.reform.dev.models.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Request object for creating a task")
public class TaskRequest {

    @JsonIgnore
    @Schema(description = "Unique task ID", example = "1")
    private Long id;


    private String caseNumber;

    @NotBlank(message = "Title is required")

    @Schema(description = "Task title", example = "Finish report")
    private String title;

    @Schema(description = "Task description", example = "Complete the financial report by Monday")
    private String description;

    @Schema(description = "Task status", example = "PENDING")
    private Status status;

    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")

    private LocalDateTime createdDate;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}
