package ru.effective_mobile.task_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
}