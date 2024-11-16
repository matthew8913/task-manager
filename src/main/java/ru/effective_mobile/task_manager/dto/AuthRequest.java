package ru.effective_mobile.task_manager.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}