package ru.effective_mobile.task_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.effective_mobile.task_manager.dto.PartialUpdateTaskRequest;
import ru.effective_mobile.task_manager.dto.TaskRequest;
import ru.effective_mobile.task_manager.dto.TaskResponse;
import ru.effective_mobile.task_manager.dto.UpdateTaskStatusRequest;
import ru.effective_mobile.task_manager.service.TaskService;

@Tag(name = "Задачи", description = "Эндпоинты для работы с задачами")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;

  /**
   * Метод создания задачи.
   *
   * @param taskRequest Запрос, содержащий данные о задаче.
   * @param bindingResult Результат валидации запроса.
   * @return ResponseEntity с созданной задачей или сообщением об ошибке.
   */
  @Operation(
      summary = "Создание задачи",
      description = "Необходима роль администратора!",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задача успешно создана",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TaskResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<?> createTask(
      @Valid @RequestBody @Parameter(description = "Данные для создания задачи")
          TaskRequest taskRequest,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
    }
    try {
      TaskResponse taskResponse = taskService.createTask(taskRequest);
      return ResponseEntity.ok(taskResponse);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка");
    }
  }

  @Operation(
      summary = "Полное обновление задачи",
      description = "Требуется роль администратора!",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задача успешно обновлена",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TaskResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> fullUpdateTask(
      @PathVariable @Parameter(description = "Идентификатор задачи") Long id,
      @RequestBody @Parameter(description = "Данные для обновления задачи")
          TaskRequest taskRequest) {
    try {
      TaskResponse taskResponse = taskService.fullUpdateTask(id, taskRequest);
      return ResponseEntity.ok(taskResponse);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @Operation(
      summary = "Частичное обновление задачи",
      description = "Необходима роль администратора!",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задача успешно обновлена",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TaskResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<TaskResponse> partialUpdateTask(
      @PathVariable @Parameter(description = "Идентификатор задачи") Long id,
      @RequestBody @Parameter(description = "Данные для частичного обновления задачи")
          PartialUpdateTaskRequest taskRequest) {
    try {
      TaskResponse taskResponse = taskService.partialUpdateTask(id, taskRequest);
      return ResponseEntity.ok(taskResponse);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @Operation(
      summary = "Удаление задачи",
      description = "Требуется роль администратора!",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Задача успешно удалена",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTask(
      @PathVariable @Parameter(description = "Идентификатор задачи") Long id) {
    try {
      taskService.deleteTask(id);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Получение задачи по идентификатору",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задача успешно получена",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TaskResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN') or (hasRole('USER'))")
  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTaskById(
      @PathVariable @Parameter(description = "Идентификатор задачи") Long id) {
    return ResponseEntity.ok(taskService.getTaskById(id));
  }

  @Operation(
      summary = "Получение всех задач с пагинацией",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задачи успешно получены",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<Page<TaskResponse>> getAllTasks(Pageable pageable) {
    return ResponseEntity.ok(taskService.getAllTasks(pageable));
  }

  @Operation(
      summary = "Получение задач по идентификатору автора с пагинацией",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задачи успешно получены",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/author/{authorId}")
  public ResponseEntity<Page<TaskResponse>> getTasksByAuthor(
      @PathVariable @Parameter(description = "Идентификатор автора") Long authorId,
      Pageable pageable) {
    return ResponseEntity.ok(taskService.getTasksByAuthor(authorId, pageable));
  }

  @Operation(
      summary = "Получение задач по идентификатору исполнителя с пагинацией",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задачи успешно получены",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize(
      "hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #assigneeId))")
  @GetMapping("/assignee/{assigneeId}")
  public ResponseEntity<Page<TaskResponse>> getTasksByAssignee(
      @PathVariable @Parameter(description = "Идентификатор исполнителя") Long assigneeId,
      Pageable pageable) {
    return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId, pageable));
  }

  /**
   * Метод для изменения статуса задачи.
   *
   * @param id Идентификатор задачи.
   * @param updateTaskStatusRequest Запрос на изменение статуса задачи.
   * @return ResponseEntity с обновленной задачей или сообщением об ошибке.
   */
  @Operation(
      summary = "Изменение статуса задачи",
      description = "Доступно только исполнителю задачи",
      security = @SecurityRequirement(name = "Bearer Authentication"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Статус задачи успешно изменен",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TaskResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PreAuthorize(
      "(hasRole('USER') and @taskService.isAssignee(authentication.name, #id))or hasRole('ADMIN')")
  @PatchMapping("/{id}/status")
  public ResponseEntity<TaskResponse> updateTaskStatus(
      @PathVariable @Parameter(description = "Идентификатор задачи") Long id,
      @RequestBody @Parameter(description = "Данные для изменения статуса задачи")
          UpdateTaskStatusRequest updateTaskStatusRequest) {
    try {
      TaskResponse taskResponse = taskService.updateTaskStatus(id, updateTaskStatusRequest);
      return ResponseEntity.ok(taskResponse);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
