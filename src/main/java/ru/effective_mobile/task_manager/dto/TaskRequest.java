package ru.effective_mobile.task_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.effective_mobile.task_manager.entities.Task;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Status is mandatory")
    private Task.Status status;

    @NotNull(message = "Priority is mandatory")
    private Task.Priority priority;

    @NotNull(message = "Author ID is mandatory")
    private Long authorId;

    private Long assigneeId;
}