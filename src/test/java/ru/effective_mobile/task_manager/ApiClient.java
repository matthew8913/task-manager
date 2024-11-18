package ru.effective_mobile.task_manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.effective_mobile.task_manager.dto.*;
import ru.effective_mobile.task_manager.entities.Task;

/**
 * Клиент для тестирования апи. Все методы содержат в параметрах данные, которые необходимо
 * передать. Последний параметр - ожидаемый ответ сервера.
 */
public class ApiClient {

  private final TestRestTemplate restTemplate;

  public ApiClient(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String login(String email, String password, HttpStatus expectedStatusCode) {
    AuthRequest authRequest = AuthRequest.builder().email(email).password(password).build();
    ResponseEntity<AuthResponse> response =
        restTemplate.postForEntity("/api/auth/login", authRequest, AuthResponse.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
    if (expectedStatusCode == HttpStatus.OK) {
      assertNotNull(response.getBody().getAccessToken());
      return response.getBody().getAccessToken();
    }
    return null;
  }

  public void register(String email, String password, HttpStatus expectedStatusCode) {
    RegisterRequest registerRequest =
        RegisterRequest.builder().email(email).password(password).build();
    ResponseEntity<?> response =
        restTemplate.postForEntity("/api/auth/register", registerRequest, Void.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
  }

  public Long createTask(
      String accessToken,
      String title,
      String description,
      Task.Status status,
      Task.Priority priority,
      Long authorId,
      Long assigneeId,
      HttpStatus expectedStatusCode) {
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title(title)
            .description(description)
            .status(status)
            .priority(priority)
            .authorId(authorId)
            .assigneeId(assigneeId)
            .build();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<TaskRequest> request = new HttpEntity<>(taskRequest, headers);

    ResponseEntity<TaskResponse> response =
        restTemplate.postForEntity("/api/tasks", request, TaskResponse.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
    if (expectedStatusCode == HttpStatus.OK) {
      return response.getBody().getId();
    }
    return null;
  }

  public void updateTask(
      String accessToken,
      Long taskId,
      String title,
      String description,
      Task.Status status,
      Task.Priority priority,
      Long authorId,
      Long assigneeId,
      HttpStatus expectedStatusCode) {
    TaskRequest taskRequest =
        TaskRequest.builder()
            .title(title)
            .description(description)
            .status(status)
            .priority(priority)
            .authorId(authorId)
            .assigneeId(assigneeId)
            .build();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<TaskRequest> request = new HttpEntity<>(taskRequest, headers);

    ResponseEntity<TaskResponse> response =
        restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.PUT, request, TaskResponse.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
  }

  public void deleteTask(String accessToken, Long taskId, HttpStatus expectedStatusCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.DELETE, request, String.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
  }

  public void getTasks(String accessToken, HttpStatus expectedStatusCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/api/tasks", HttpMethod.GET, request, String.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
  }

  public void getTask(String accessToken, Long taskId, HttpStatus expectedStatusCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange("/api/tasks/" + taskId, HttpMethod.GET, request, String.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
  }

  public void logout(String accessToken, String email, HttpStatus expectedStatusCode) {
    LogoutRequest logoutRequest = new LogoutRequest(email);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<LogoutRequest> request = new HttpEntity<>(logoutRequest, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/auth/logout", request, String.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
  }

  public Long addComment(
      String accessToken, Long taskId, String text, HttpStatus expectedStatusCode) {
    CommentRequest commentRequest = CommentRequest.builder().taskId(taskId).content(text).build();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<CommentRequest> request = new HttpEntity<>(commentRequest, headers);

    ResponseEntity<CommentResponse> response =
        restTemplate.postForEntity("/api/comments", request, CommentResponse.class);
    assertEquals(expectedStatusCode, response.getStatusCode());
    if (expectedStatusCode == HttpStatus.OK) {
      return response.getBody().getId();
    }
    return null;
  }
}
