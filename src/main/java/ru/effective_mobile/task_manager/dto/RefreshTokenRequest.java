package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Запрос на обновление токена")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
  @Schema(description = "Refresh токен", example = "refresh_token_example")
  private String refreshToken;
}
