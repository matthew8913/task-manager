package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.effective_mobile.task_manager.entities.Task;

@Data
@Schema(description = "Запрос на создание или обновление задачи")
@Builder
public class TaskRequest {
  @NotBlank(message = "Title is mandatory")
  @Schema(description = "Заголовок задачи", example = "Задача 1")
  private String title;

  @NotBlank(message = "Description is mandatory")
  @Schema(description = "Описание задачи", example = "Пример описания задачи")
  private String description;

  @NotNull(message = "Status is mandatory")
  @Schema(description = "Статус задачи", example = "IN_PROGRESS")
  private Task.Status status;

  @NotNull(message = "Priority is mandatory")
  @Schema(description = "Приоритет задачи", example = "HIGH")
  private Task.Priority priority;

  @NotNull(message = "Author ID is mandatory")
  @Schema(description = "Идентификатор автора", example = "1")
  private Long authorId;

  @Schema(description = "Идентификатор исполнителя", example = "2")
  private Long assigneeId;
}
