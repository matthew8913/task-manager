package ru.effective_mobile.task_manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.effective_mobile.task_manager.dto.*;
import ru.effective_mobile.task_manager.service.TaskService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private TaskService taskService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void testFullUpdateTaskSuccess() throws Exception {
    Long taskId = 1L;
    TaskRequest taskRequest =
        TaskRequest.builder().title("Test task").description("Test Description").build();

    TaskResponse taskResponse = new TaskResponse();
    taskResponse.setId(taskId);
    taskResponse.setTitle("Updated Task");
    taskResponse.setDescription("Updated Description");

    when(taskService.fullUpdateTask(any(), any())).thenReturn(taskResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(taskId))
        .andExpect(jsonPath("$.title").value("Updated Task"))
        .andExpect(jsonPath("$.description").value("Updated Description"));
  }

  @Test
  public void testFullUpdateTaskFailure() throws Exception {
    Long taskId = 1L;
    TaskRequest taskRequest =
        TaskRequest.builder().title("Updated task").description("Updated Description").build();

    when(taskService.fullUpdateTask(any(), any()))
        .thenThrow(new IllegalArgumentException("Invalid data"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testPartialUpdateTaskSuccess() throws Exception {
    Long taskId = 1L;
    PartialUpdateTaskRequest taskRequest =
        PartialUpdateTaskRequest.builder().title("Partial Updated Task").build();

    TaskResponse taskResponse = new TaskResponse();
    taskResponse.setId(taskId);
    taskResponse.setTitle("Partial Updated Task");
    taskResponse.setDescription("Original Description");

    when(taskService.partialUpdateTask(any(), any())).thenReturn(taskResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(taskId))
        .andExpect(jsonPath("$.title").value("Partial Updated Task"))
        .andExpect(jsonPath("$.description").value("Original Description"));
  }

  @Test
  public void testPartialUpdateTaskFailure() throws Exception {
    Long taskId = 1L;
    PartialUpdateTaskRequest taskRequest =
        PartialUpdateTaskRequest.builder().title("Partial Updated Task").build();

    when(taskService.partialUpdateTask(any(), any()))
        .thenThrow(new IllegalArgumentException("Invalid data"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testDeleteTaskSuccess() throws Exception {
    Long taskId = 1L;

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/tasks/{id}", taskId))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testGetTaskByIdSuccess() throws Exception {
    Long taskId = 1L;
    TaskResponse taskResponse = new TaskResponse();
    taskResponse.setId(taskId);
    taskResponse.setTitle("Test Task");
    taskResponse.setDescription("Test Description");

    when(taskService.getTaskById(any())).thenReturn(taskResponse);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/tasks/{id}", taskId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(taskId))
        .andExpect(jsonPath("$.title").value("Test Task"))
        .andExpect(jsonPath("$.description").value("Test Description"));
  }

  @Test
  public void testGetAllTasksSuccess() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    TaskResponse taskResponse = new TaskResponse();
    taskResponse.setId(1L);
    taskResponse.setTitle("Test Task");
    taskResponse.setDescription("Test Description");

    Page<TaskResponse> tasksPage =
        new PageImpl<>(Collections.singletonList(taskResponse), pageable, 1);

    when(taskService.getAllTasks(any())).thenReturn(tasksPage);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/tasks").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1L))
        .andExpect(jsonPath("$.content[0].title").value("Test Task"))
        .andExpect(jsonPath("$.content[0].description").value("Test Description"));
  }

  @Test
  public void testGetTasksByAuthorSuccess() throws Exception {
    Long authorId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    TaskResponse taskResponse = new TaskResponse();
    taskResponse.setId(1L);
    taskResponse.setTitle("Test Task");
    taskResponse.setDescription("Test Description");

    Page<TaskResponse> tasksPage =
        new PageImpl<>(Collections.singletonList(taskResponse), pageable, 1);

    when(taskService.getTasksByAuthor(any(), any())).thenReturn(tasksPage);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/tasks/author/{authorId}", authorId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1L))
        .andExpect(jsonPath("$.content[0].title").value("Test Task"))
        .andExpect(jsonPath("$.content[0].description").value("Test Description"));
  }

  @Test
  public void testGetTasksByAssigneeSuccess() throws Exception {
    Long assigneeId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    TaskResponse taskResponse = new TaskResponse();
    taskResponse.setId(1L);
    taskResponse.setTitle("Test Task");
    taskResponse.setDescription("Test Description");

    Page<TaskResponse> tasksPage =
        new PageImpl<>(Collections.singletonList(taskResponse), pageable, 1);

    when(taskService.getTasksByAssignee(any(), any())).thenReturn(tasksPage);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/tasks/assignee/{assigneeId}", assigneeId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1L))
        .andExpect(jsonPath("$.content[0].title").value("Test Task"))
        .andExpect(jsonPath("$.content[0].description").value("Test Description"));
  }
}
