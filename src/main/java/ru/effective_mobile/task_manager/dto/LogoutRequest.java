package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на выход")
public class LogoutRequest {
  @Schema(description = "Email пользователя", example = "user@example.com")
  private String email;
}
