package ru.effective_mobile.task_manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import ru.effective_mobile.task_manager.entities.Task;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskManagementSystemTest {

  @Autowired private TestRestTemplate restTemplate;

  private ApiClient apiClient;

  @BeforeEach
  public void setUp() {
    apiClient = new ApiClient(restTemplate);
  }

  @Test
  public void testScenario() {
    // Логин под админом "admin@example.com" "adminPassword"
    String accessTokenAdmin = apiClient.login("admin@example.com", "adminPassword", HttpStatus.OK);

    // Регистрация пользователей
    apiClient.register("user1@example.com", "password1", HttpStatus.OK);
    apiClient.register("user2@example.com", "password2", HttpStatus.OK);

    // Создание задачи админом для пользователя 1
    Long taskIdUser1 =
        apiClient.createTask(
            accessTokenAdmin,
            "Task User 1",
            "Description User 1",
            Task.Status.PENDING,
            Task.Priority.MEDIUM,
            2L,
            2L,
            HttpStatus.OK);

    // Создание задачи админом для пользователя 2
    Long taskIdUser2 =
        apiClient.createTask(
            accessTokenAdmin,
            "Task User 2",
            "Description User 2",
            Task.Status.PENDING,
            Task.Priority.MEDIUM,
            3L,
            3L,
            HttpStatus.OK);

    // Логин под пользователем 1
    String accessTokenUser1 = apiClient.login("user1@example.com", "password1", HttpStatus.OK);

    // Логин под пользователем 2
    String accessTokenUser2 = apiClient.login("user2@example.com", "password2", HttpStatus.OK);

    // Попытка любым пользователем создания задачи (должно быть запрещено)
    apiClient.createTask(
        accessTokenUser1,
        "Task User 1",
        "Description User 1",
        Task.Status.PENDING,
        Task.Priority.MEDIUM,
        2L,
        2L,
        HttpStatus.FORBIDDEN);
    apiClient.createTask(
        accessTokenUser2,
        "Task User 2",
        "Description User 2",
        Task.Status.PENDING,
        Task.Priority.MEDIUM,
        3L,
        3L,
        HttpStatus.FORBIDDEN);

    // Получение любым пользователем списка задач
    apiClient.getTasks(accessTokenUser1, HttpStatus.FORBIDDEN);
    apiClient.getTasks(accessTokenUser2, HttpStatus.FORBIDDEN);

    // Попытка пользователя 1 редактировать задачу пользователя 2 (должно быть запрещено)
    apiClient.updateTask(
        accessTokenUser1,
        taskIdUser2,
        "Updated Task User 2",
        "Updated Description User 2",
        Task.Status.IN_PROGRESS,
        Task.Priority.HIGH,
        3L,
        3L,
        HttpStatus.FORBIDDEN);

    // Попытка пользователя 1 удалить задачу пользователя 2 (должно быть запрещено)
    apiClient.deleteTask(accessTokenUser1, taskIdUser2, HttpStatus.FORBIDDEN);

    // Попытка пользователя 1 редактировать задачу пользователя 1
    apiClient.updateTask(
        accessTokenUser1,
        taskIdUser1,
        "Updated Task User 1",
        "Updated Description User 1",
        Task.Status.IN_PROGRESS,
        Task.Priority.HIGH,
        2L,
        2L,
        HttpStatus.FORBIDDEN);

    // Попытка админа редактировать задачу пользователя 1
    apiClient.updateTask(
        accessTokenAdmin,
        taskIdUser1,
        "Updated Task User 1 by Admin",
        "Updated Description User 1 by Admin",
        Task.Status.IN_PROGRESS,
        Task.Priority.HIGH,
        2L,
        2L,
        HttpStatus.OK);

    // Попытка пользователя 1 обновить свой accessToken
    // String newAccessTokenUser1 = apiClient.refreshToken("refreshTokenUser1", HttpStatus.OK);

    // Попытка пользователя 1 оставить комментарий к своей задаче (разрешено)
    apiClient.addComment(
        accessTokenUser1, taskIdUser1, "Comment by User 1 on Task 1", HttpStatus.OK);

    // Попытка пользователя 1 оставить комментарий к задаче пользователя 2 (должно быть запрещено)
    apiClient.addComment(
        accessTokenUser1, taskIdUser2, "Comment by User 1 on Task 2", HttpStatus.FORBIDDEN);

    // Попытка пользователя 1 получить свою задачу (разрешено)
    apiClient.getTask(accessTokenUser1, taskIdUser1, HttpStatus.OK);

    // Попытка админа удалить задачу пользователя 1
    apiClient.deleteTask(accessTokenAdmin, taskIdUser1, HttpStatus.NO_CONTENT);

    // Логаут всех пользователей, участвующих в тестировании
    apiClient.logout(accessTokenUser1, "user1@example.com", HttpStatus.OK);
    apiClient.logout(accessTokenUser2, "user2@example.com", HttpStatus.OK);
    apiClient.logout(accessTokenAdmin, "admin@example.com", HttpStatus.OK);
  }
}
