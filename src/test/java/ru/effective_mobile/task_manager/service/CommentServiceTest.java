package ru.effective_mobile.task_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.effective_mobile.task_manager.dto.CommentRequest;
import ru.effective_mobile.task_manager.dto.CommentResponse;
import ru.effective_mobile.task_manager.entities.Comment;
import ru.effective_mobile.task_manager.entities.Task;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.CommentRepository;
import ru.effective_mobile.task_manager.repository.TaskRepository;
import ru.effective_mobile.task_manager.repository.UserRepository;

class CommentServiceTest {

  @Mock private CommentRepository commentRepository;

  @Mock private TaskRepository taskRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private CommentService commentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetCommentsByTask() {
    Long taskId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    User author =
        User.builder()
            .id(1L)
            .email("author@email.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    Task task =
        Task.builder()
            .id(1L)
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Comment comment =
        Comment.builder()
            .id(1L)
            .content("Content")
            .task(task)
            .author(author)
            .createdAt(LocalDateTime.now())
            .build();

    Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment));

    when(commentRepository.findByTaskId(taskId, pageable)).thenReturn(commentPage);

    Page<CommentResponse> responsePage = commentService.getCommentsByTask(taskId, pageable);

    assertThat(responsePage).isNotEmpty();
    assertThat(responsePage.getContent().getFirst().getId()).isEqualTo(1L);
    assertThat(responsePage.getContent().getFirst().getContent()).isEqualTo("Content");
    assertThat(responsePage.getContent().getFirst().getTaskId()).isEqualTo(1L);
    assertThat(responsePage.getContent().getFirst().getAuthorId()).isEqualTo(1L);
  }
}
