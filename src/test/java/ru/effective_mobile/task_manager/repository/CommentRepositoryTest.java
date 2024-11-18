package ru.effective_mobile.task_manager.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.effective_mobile.task_manager.entities.Comment;
import ru.effective_mobile.task_manager.entities.Task;
import ru.effective_mobile.task_manager.entities.User;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommentRepositoryTest {

  @Autowired private CommentRepository commentRepository;

  @Autowired private TaskRepository taskRepository;

  @Autowired private UserRepository userRepository;

  @Test
  void testFindByTaskId() {
    User author =
        User.builder().email("author@email.com").password("password").role(User.Role.ADMIN).build();
    userRepository.save(author);

    Task task =
        Task.builder()
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    taskRepository.save(task);

    Comment comment =
        Comment.builder()
            .content("Comment Content")
            .task(task)
            .author(author)
            .createdAt(LocalDateTime.now())
            .build();
    commentRepository.save(comment);

    Pageable pageable = PageRequest.of(0, 10);
    Page<Comment> foundComments = commentRepository.findByTaskId(task.getId(), pageable);

    assertThat(foundComments).isNotEmpty();
    assertThat(foundComments.getContent().getFirst().getId()).isGreaterThan(0);
    assertThat(foundComments.getContent().getFirst().getContent()).isEqualTo("Comment Content");
  }

  @Test
  void testValidationException() {
    Comment comment = Comment.builder().content("").createdAt(LocalDateTime.now()).build();

    assertThatThrownBy(() -> commentRepository.save(comment)).isInstanceOf(Exception.class);
  }
}
