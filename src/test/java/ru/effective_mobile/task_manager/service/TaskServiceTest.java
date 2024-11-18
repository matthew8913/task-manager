package ru.effective_mobile.task_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
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
import ru.effective_mobile.task_manager.dto.PartialUpdateTaskRequest;
import ru.effective_mobile.task_manager.dto.TaskRequest;
import ru.effective_mobile.task_manager.dto.TaskResponse;
import ru.effective_mobile.task_manager.dto.UpdateTaskStatusRequest;
import ru.effective_mobile.task_manager.entities.Task;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.TaskRepository;
import ru.effective_mobile.task_manager.repository.UserRepository;

class TaskServiceTest {

  @Mock private TaskRepository taskRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private TaskService taskService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateTaskWithInvalidAuthor() {
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .authorId(1L)
            .assigneeId(2L)
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.createTask(taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Author not found");
  }

  @Test
  void testCreateTaskWithInvalidAssignee() {
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .authorId(1L)
            .assigneeId(2L)
            .build();

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(author));
    when(userRepository.findById(2L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.createTask(taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Assignee not found");
  }

  @Test
  void testPartialUpdateTask() {
    Long taskId = 1L;
    PartialUpdateTaskRequest taskRequest =
        PartialUpdateTaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .assigneeId(2L)
            .build();

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    User assignee =
        User.builder()
            .id(2L)
            .email("assignee@example.com")
            .password("password")
            .role(User.Role.USER)
            .build();

    Task task =
        Task.builder()
            .id(1L)
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    TaskResponse response = taskService.partialUpdateTask(taskId, taskRequest);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo("Updated Title");
    assertThat(response.getDescription()).isEqualTo("Updated Description");
    assertThat(response.getStatus()).isEqualTo(Task.Status.IN_PROGRESS);
    assertThat(response.getPriority()).isEqualTo(Task.Priority.MEDIUM);
    assertThat(response.getAuthorId()).isEqualTo(1L);
    assertThat(response.getAssigneeId()).isEqualTo(2L);
  }

  @Test
  void testPartialUpdateTaskWithInvalidTaskId() {
    Long taskId = 1L;
    PartialUpdateTaskRequest taskRequest =
        PartialUpdateTaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .assigneeId(2L)
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.partialUpdateTask(taskId, taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Task not found");
  }

  @Test
  void testPartialUpdateTaskWithInvalidAssignee() {
    Long taskId = 1L;
    PartialUpdateTaskRequest taskRequest =
        PartialUpdateTaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .assigneeId(2L)
            .build();

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
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

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(2L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.partialUpdateTask(taskId, taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Assignee not found");
  }

  @Test
  void testFullUpdateTask() {
    Long taskId = 1L;
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .authorId(1L)
            .assigneeId(2L)
            .build();

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    User assignee =
        User.builder()
            .id(2L)
            .email("assignee@example.com")
            .password("password")
            .role(User.Role.USER)
            .build();

    Task task =
        Task.builder()
            .id(1L)
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(1L)).thenReturn(Optional.of(author));
    when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    TaskResponse response = taskService.fullUpdateTask(taskId, taskRequest);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo("Updated Title");
    assertThat(response.getDescription()).isEqualTo("Updated Description");
    assertThat(response.getStatus()).isEqualTo(Task.Status.IN_PROGRESS);
    assertThat(response.getPriority()).isEqualTo(Task.Priority.MEDIUM);
    assertThat(response.getAuthorId()).isEqualTo(1L);
    assertThat(response.getAssigneeId()).isEqualTo(2L);
  }

  @Test
  void testFullUpdateTaskWithInvalidTask() {
    Long taskId = 1L;
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .authorId(1L)
            .assigneeId(2L)
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.fullUpdateTask(taskId, taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Task not found");
  }

  @Test
  void testFullUpdateTaskWithInvalidAuthor() {
    Long taskId = 1L;
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .authorId(1L)
            .assigneeId(2L)
            .build();

    Task task =
        Task.builder()
            .id(1L)
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.fullUpdateTask(taskId, taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Author not found");
  }

  @Test
  void testFullUpdateTaskWithInvalidAssignee() {
    Long taskId = 1L;
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .authorId(1L)
            .assigneeId(2L)
            .build();

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
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

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(userRepository.findById(1L)).thenReturn(Optional.of(author));
    when(userRepository.findById(2L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.fullUpdateTask(taskId, taskRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Assignee not found");
  }

  @Test
  void testDeleteTask() {
    Long taskId = 1L;

    when(taskRepository.existsById(taskId)).thenReturn(true);

    taskService.deleteTask(taskId);

    verify(taskRepository, times(1)).deleteById(taskId);
  }

  @Test
  void testDeleteTaskWithInvalidTask() {
    Long taskId = 1L;

    when(taskRepository.existsById(taskId)).thenReturn(false);

    assertThatThrownBy(() -> taskService.deleteTask(taskId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Task not found");
  }

  @Test
  void testGetTaskById() {
    Long taskId = 1L;

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
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

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    TaskResponse response = taskService.getTaskById(taskId);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo("Task Title");
    assertThat(response.getDescription()).isEqualTo("Task Description");
    assertThat(response.getStatus()).isEqualTo(Task.Status.PENDING);
    assertThat(response.getPriority()).isEqualTo(Task.Priority.HIGH);
    assertThat(response.getAuthorId()).isEqualTo(1L);
  }

  @Test
  void testGetTaskByIdWithInvalidTask() {
    Long taskId = 1L;

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.getTaskById(taskId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Task not found");
  }

  @Test
  void testGetTasksByAuthor() {
    Long authorId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    Task task1 =
        Task.builder()
            .id(1L)
            .title("Task Title 1")
            .description("Task Description 1")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Task task2 =
        Task.builder()
            .id(2L)
            .title("Task Title 2")
            .description("Task Description 2")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .author(author)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1, task2));

    when(taskRepository.findByAuthorId(authorId, pageable)).thenReturn(taskPage);

    Page<TaskResponse> responsePage = taskService.getTasksByAuthor(authorId, pageable);

    assertThat(responsePage).isNotEmpty();
    assertThat(responsePage.getContent().size()).isEqualTo(2);
    assertThat(responsePage.getContent().get(0).getId()).isEqualTo(1L);
    assertThat(responsePage.getContent().get(0).getTitle()).isEqualTo("Task Title 1");
    assertThat(responsePage.getContent().get(1).getId()).isEqualTo(2L);
    assertThat(responsePage.getContent().get(1).getTitle()).isEqualTo("Task Title 2");
  }

  @Test
  void testGetTasksByAssignee() {
    Long assigneeId = 2L;
    Pageable pageable = PageRequest.of(0, 10);

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    User assignee =
        User.builder()
            .id(2L)
            .email("assignee@example.com")
            .password("password")
            .role(User.Role.USER)
            .build();

    Task task1 =
        Task.builder()
            .id(1L)
            .title("Task Title 1")
            .description("Task Description 1")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Task task2 =
        Task.builder()
            .id(2L)
            .title("Task Title 2")
            .description("Task Description 2")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1, task2));

    when(taskRepository.findByAssigneeId(assigneeId, pageable)).thenReturn(taskPage);

    Page<TaskResponse> responsePage = taskService.getTasksByAssignee(assigneeId, pageable);

    assertThat(responsePage).isNotEmpty();
    assertThat(responsePage.getContent().size()).isEqualTo(2);
    assertThat(responsePage.getContent().get(0).getId()).isEqualTo(1L);
    assertThat(responsePage.getContent().get(0).getTitle()).isEqualTo("Task Title 1");
    assertThat(responsePage.getContent().get(1).getId()).isEqualTo(2L);
    assertThat(responsePage.getContent().get(1).getTitle()).isEqualTo("Task Title 2");
  }

  @Test
  void testGetAllTasks() {
    Pageable pageable = PageRequest.of(0, 10);

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    Task task1 =
        Task.builder()
            .id(1L)
            .title("Task Title 1")
            .description("Task Description 1")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Task task2 =
        Task.builder()
            .id(2L)
            .title("Task Title 2")
            .description("Task Description 2")
            .status(Task.Status.IN_PROGRESS)
            .priority(Task.Priority.MEDIUM)
            .author(author)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1, task2));

    when(taskRepository.findAll(pageable)).thenReturn(taskPage);

    Page<TaskResponse> responsePage = taskService.getAllTasks(pageable);

    assertThat(responsePage).isNotEmpty();
    assertThat(responsePage.getContent().size()).isEqualTo(2);
    assertThat(responsePage.getContent().get(0).getId()).isEqualTo(1L);
    assertThat(responsePage.getContent().get(0).getTitle()).isEqualTo("Task Title 1");
    assertThat(responsePage.getContent().get(1).getId()).isEqualTo(2L);
    assertThat(responsePage.getContent().get(1).getTitle()).isEqualTo("Task Title 2");
  }

  @Test
  void testIsAssignee() {
    Long taskId = 1L;
    String username = "assignee@example.com";

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    User assignee =
        User.builder()
            .id(2L)
            .email("assignee@example.com")
            .password("password")
            .role(User.Role.USER)
            .build();

    Task task =
        Task.builder()
            .id(1L)
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    boolean isAssignee = taskService.isAssignee(username, taskId);

    assertThat(isAssignee).isTrue();
  }

  @Test
  void testIsAssigneeWithDifferentUsername() {
    Long taskId = 1L;
    String username = "different@example.com";

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
            .password("password")
            .role(User.Role.ADMIN)
            .build();

    User assignee =
        User.builder()
            .id(2L)
            .email("assignee@example.com")
            .password("password")
            .role(User.Role.USER)
            .build();

    Task task =
        Task.builder()
            .id(1L)
            .title("Task Title")
            .description("Task Description")
            .status(Task.Status.PENDING)
            .priority(Task.Priority.HIGH)
            .author(author)
            .assignee(assignee)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    boolean isAssignee = taskService.isAssignee(username, taskId);

    assertThat(isAssignee).isFalse();
  }

  @Test
  void testUpdateTaskStatus() {
    Long taskId = 1L;
    UpdateTaskStatusRequest updateTaskStatusRequest =
        UpdateTaskStatusRequest.builder().status(Task.Status.COMPLETED).build();

    User author =
        User.builder()
            .id(1L)
            .email("author@example.com")
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

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    TaskResponse response = taskService.updateTaskStatus(taskId, updateTaskStatusRequest);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getStatus()).isEqualTo(Task.Status.COMPLETED);
  }
}
