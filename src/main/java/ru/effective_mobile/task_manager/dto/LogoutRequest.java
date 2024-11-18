package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Запрос на выход")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
  @Schema(description = "Email пользователя", example = "user@example.com")
  private String email;
}
