package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Ответ с информацией о комментарии")
public class CommentResponse {
  @Schema(description = "Идентификатор комментария", example = "1")
  private Long id;

  @Schema(description = "Содержание комментария", example = "Это пример комментария")
  private String content;

  @Schema(description = "Идентификатор задачи", example = "1")
  private Long taskId;

  @Schema(description = "Идентификатор автора", example = "2")
  private Long authorId;

  @Schema(description = "Дата и время создания комментария", example = "2023-10-01T12:34:56")
  private LocalDateTime createdAt;
}
