package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ на обновление токена")
public class RefreshTokenResponse {
  @Schema(description = "Новый access токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
  private String accessToken;
}
