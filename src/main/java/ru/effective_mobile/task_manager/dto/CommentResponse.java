package ru.effective_mobile.task_manager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Long taskId;
    private Long authorId;
    private LocalDateTime createdAt;
}