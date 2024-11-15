package ru.effective_mobile.task_manager.dto;

import lombok.Data;
import ru.effective_mobile.task_manager.entities.Task;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private Long authorId;
    private Long assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}