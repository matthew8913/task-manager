package ru.effective_mobile.task_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effective_mobile.task_manager.dto.CommentRequest;
import ru.effective_mobile.task_manager.dto.CommentResponse;
import ru.effective_mobile.task_manager.service.CommentService;

/**
 * Контроллер для работы с комментариями.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Создает новый комментарий.
     *
     * @param commentRequest Запрос на создание комментария.
     * @return Ответ с созданным комментарием или сообщение об ошибке.
     */
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {
        try {
            CommentResponse commentResponse = commentService.createComment(commentRequest);
            return ResponseEntity.ok(commentResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    /**
     * Получает список комментариев по идентификатору задачи с пагинацией.
     *
     * @param taskId   Идентификатор задачи.
     * @param pageable Объект пагинации.
     * @return Страница с комментариями или сообщение об ошибке.
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId, Pageable pageable) {
        try {
            Page<CommentResponse> comments = commentService.getCommentsByTask(taskId, pageable);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}