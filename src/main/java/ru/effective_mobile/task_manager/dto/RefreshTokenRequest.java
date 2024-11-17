package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление токена")
public class RefreshTokenRequest {
  @Schema(description = "Refresh токен", example = "refresh_token_example")
  private String refreshToken;
}
