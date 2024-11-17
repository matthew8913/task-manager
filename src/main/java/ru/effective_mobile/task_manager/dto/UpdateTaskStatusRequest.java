package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.effective_mobile.task_manager.entities.Task;

@Data
@Schema(description = "Запрос на изменение статуса задачи")
public class UpdateTaskStatusRequest {
  @Schema(description = "Новый статус задачи", example = "IN_PROGRESS")
  private Task.Status status;
}
