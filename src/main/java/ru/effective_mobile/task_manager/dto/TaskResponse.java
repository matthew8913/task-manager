package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import ru.effective_mobile.task_manager.entities.Task;

@Data
@Schema(description = "Ответ с информацией о задаче")
public class TaskResponse {
  @Schema(description = "Идентификатор задачи", example = "1")
  private Long id;

  @Schema(description = "Заголовок задачи", example = "Задача 1")
  private String title;

  @Schema(description = "Описание задачи", example = "Это пример описания задачи")
  private String description;

  @Schema(description = "Статус задачи", example = "IN_PROGRESS")
  private Task.Status status;

  @Schema(description = "Приоритет задачи", example = "HIGH")
  private Task.Priority priority;

  @Schema(description = "Идентификатор автора", example = "1")
  private Long authorId;

  @Schema(description = "Идентификатор исполнителя", example = "2")
  private Long assigneeId;

  @Schema(description = "Дата и время создания задачи", example = "2023-10-01T12:34:56")
  private LocalDateTime createdAt;

  @Schema(
      description = "Дата и время последнего обновления задачи",
      example = "2023-10-02T14:30:00")
  private LocalDateTime updatedAt;
}
