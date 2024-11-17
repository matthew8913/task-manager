package ru.effective_mobile.task_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class AuthRequest {
  @Schema(
      description = "Уникальный email пользователя",
      example = "ivanov@mail.ru",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @Schema(
      description = "Пароль пользователя",
      example = "12345678",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
}
