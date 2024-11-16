package ru.effective_mobile.task_manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.effective_mobile.task_manager.dto.TaskRequest;
import ru.effective_mobile.task_manager.dto.TaskResponse;
import ru.effective_mobile.task_manager.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * Метод создания задачи.
     *
     * @param taskRequest   Запрос, содержащий данные о задаче.
     * @param bindingResult Результат валидации запроса.
     * @return ResponseEntity с созданной задачей или сообщением об ошибке.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        try {
            TaskResponse taskResponse = taskService.createTask(taskRequest);
            return ResponseEntity.ok(taskResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #id))")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> fullUpdateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.fullUpdateTask(id, taskRequest);
            return ResponseEntity.ok(taskResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #id))")
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> partialUpdateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.partialUpdateTask(id, taskRequest);
            return ResponseEntity.ok(taskResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #id))")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try{
            taskService.deleteTask(id);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #id))")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByAuthor(@PathVariable Long authorId, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByAuthor(authorId, pageable));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #assigneeId))")
    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByAssignee(@PathVariable Long assigneeId, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId, pageable));
    }
}
