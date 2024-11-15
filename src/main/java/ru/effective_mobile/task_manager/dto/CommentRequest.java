package ru.effective_mobile.task_manager.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long taskId;
    private Long authorId;
}