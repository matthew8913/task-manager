package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание комментария")
@Builder
public class CommentRequest {
  @Schema(description = "Содержание комментария", example = "Это пример комментария")
  private String content;

  @Schema(description = "Идентификатор задачи", example = "1")
  private Long taskId;

  @Schema(description = "Идентификатор автора", example = "2")
  private Long authorId;
}
