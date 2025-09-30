package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.Status;
import uk.gov.hmcts.reform.dev.service.TaskService;


import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.mockito.Mockito;

@WebMvcTest(controllers = TaskController.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the Service layer dependency; this ensures the test stays at the Controller layer
    @MockitoBean
    private TaskService taskService;

    private TaskResponse sampleTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // initialize @Mock

        sampleTask = new TaskResponse();
        sampleTask.setId(1L);
        sampleTask.setTitle("Sample Task");
        sampleTask.setDescription("Sample Description");
        sampleTask.setStatus(Status.PENDING);
    }


    @Test
    void getTaskByIdShouldReturnTask() throws Exception {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Sample Task"));
    }


    @Test
    void getAllTasksShouldReturnList() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(Collections.singletonList(sampleTask));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1));
    }


    @Test
    void createTaskShouldReturnCreated() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setStatus(Status.PENDING);

        Mockito.when(taskService.createTask(any(TaskRequest.class))).thenReturn(sampleTask);

        mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Sample Task"));
    }

    @Test
    void updateStatusShouldReturnUpdatedTask() throws Exception {
        sampleTask.setStatus(Status.COMPLETED);

        Mockito.when(taskService.updateTaskStatus(eq(1L), eq("COMPLETED"))).thenReturn(sampleTask);

        mockMvc.perform(put("/tasks/1")
                            .param("status", "COMPLETED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteTaskShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isNoContent());
    }


}
