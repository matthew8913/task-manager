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
import ru.effective_mobile.task_manager.entities.Task;
import ru.effective_mobile.task_manager.entities.User;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TaskRepositoryTest {

  @Autowired private TaskRepository taskRepository;

  @Autowired private UserRepository userRepository;

  @Test
  void testFindByAuthorId() {
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

    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> foundTasks = taskRepository.findByAuthorId(author.getId(), pageable);

    assertThat(foundTasks).isNotEmpty();
    assertThat(foundTasks.getContent().getFirst().getId()).isGreaterThan(0);
    assertThat(foundTasks.getContent().getFirst().getTitle()).isEqualTo("Task Title");
  }

  @Test
  void testFindByAssigneeId() {
    User author =
        User.builder().email("author@email.com").password("password").role(User.Role.ADMIN).build();
    userRepository.save(author);

    User assignee =
        User.builder()
            .email("assignee@email.com")
            .password("password")
            .role(User.Role.USER)
            .build();
    userRepository.save(assignee);

    Task task =
        Task.builder()
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    taskRepository.save(task);

    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> foundTasks = taskRepository.findByAssigneeId(assignee.getId(), pageable);

    assertThat(foundTasks).isNotEmpty();
    assertThat(foundTasks.getContent().getFirst().getId()).isGreaterThan(0);
    assertThat(foundTasks.getContent().getFirst().getTitle()).isEqualTo("Task Title");
  }

  @Test
  void testValidationException() {
    Task task =
        Task.builder()
            .title("") // Заголовок пуст
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    assertThatThrownBy(() -> taskRepository.save(task)).isInstanceOf(Exception.class);
  }
}
