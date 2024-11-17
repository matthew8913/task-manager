package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.effective_mobile.task_manager.entities.Task;

@Data
@Schema(description = "Запрос на частичное обновление задачи")
public class PartialUpdateTaskRequest {
  @Schema(description = "Заголовок задачи", example = "Задача 1")
  private String title;

  @Schema(description = "Описание задачи", example = "Пример описания задачи")
  private String description;

  @Schema(description = "Статус задачи", example = "IN_PROGRESS")
  private Task.Status status;

  @Schema(description = "Приоритет задачи", example = "HIGH")
  private Task.Priority priority;

  @Schema(description = "Идентификатор автора", example = "1")
  private Long authorId;

  @Schema(description = "Идентификатор исполнителя", example = "2")
  private Long assigneeId;
}
