package ru.effective_mobile.task_manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.effective_mobile.task_manager.dto.CommentRequest;
import ru.effective_mobile.task_manager.dto.CommentResponse;
import ru.effective_mobile.task_manager.service.CommentService;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CommentService commentService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void testCreateCommentSuccess() throws Exception {
    CommentRequest commentRequest =
        CommentRequest.builder().taskId(1L).content("Test comment").build();

    CommentResponse commentResponse = new CommentResponse();
    commentResponse.setId(1L);
    commentResponse.setContent("Test comment");

    when(commentService.createComment(any())).thenReturn(commentResponse);

    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.content").value("Test comment"));
  }

  @Test
  public void testCreateCommentFailure() throws Exception {
    CommentRequest commentRequest =
        CommentRequest.builder().taskId(1L).content("Test comment").build();

    when(commentService.createComment(any()))
        .thenThrow(new IllegalArgumentException("Invalid data"));

    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid data"));
  }
}
