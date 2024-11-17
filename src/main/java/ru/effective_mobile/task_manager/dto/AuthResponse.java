package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ на аутентификацию")
public class AuthResponse {
  @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
  private String token;

  @Schema(description = "Refresh токен", example = "refresh_token_example")
  private String refreshToken;
}
