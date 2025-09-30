package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.dev.models.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response object for task operations")
@NoArgsConstructor
public class TaskResponse {

    @Schema(description = "Unique task ID", example = "1")
    private Long id;

    @Schema(description = "Task title", example = "Finish report")
    private String title;

    @Schema(description = "Case number", example = "Auto generated case number ")
    private String caseNumber;

    @Schema(description = "Task description", example = "Complete the financial report by Tuesday")
    private String description;

    @Schema(description = "Task status", example = "PENDING")
    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dueDate;

    // Constructor
    public TaskResponse(Long id, String title,String caseNumber,  String description, Status status,
                        LocalDateTime dueDate) {
        this.id = id;
        this.title = title;
        this.caseNumber = caseNumber;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

}
