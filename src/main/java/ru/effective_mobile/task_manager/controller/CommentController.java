package ru.effective_mobile.task_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.effective_mobile.task_manager.dto.CommentRequest;
import ru.effective_mobile.task_manager.dto.CommentResponse;
import ru.effective_mobile.task_manager.service.CommentService;

/** Контроллер для работы с комментариями. */
@Tag(name = "Комментарии", description = "Эндпоинты для работы с комментариями")
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
  @Operation(
      summary = "Создание комментария",
      description = "Необходимо являться исполнителем, либо администратором!")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Комментарий успешно создан",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CommentResponse.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @PostMapping
  @PreAuthorize(
      "hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #commentRequest.taskId))")
  public ResponseEntity<?> createComment(
      @RequestBody @Parameter(description = "Данные для создания комментария")
          CommentRequest commentRequest) {
    try {
      CommentResponse commentResponse = commentService.createComment(commentRequest);
      return ResponseEntity.ok(commentResponse);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка");
    }
  }

  /**
   * Получает список комментариев по идентификатору задачи с пагинацией.
   *
   * @param taskId Идентификатор задачи.
   * @param pageable Объект пагинации.
   * @return Страница с комментариями или сообщение об ошибке.
   */
  @Operation(
      summary = "Получение комментариев по Id задачи",
      description = "Необходимо являться исполнителем, либо администратором!")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Комментарии успешно получены",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Неверные данные запроса",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content)
      })
  @GetMapping("/task/{taskId}")
  @PreAuthorize(
      "hasRole('ADMIN') or (hasRole('USER') and @taskService.isAssignee(authentication.name, #taskId))")
  public ResponseEntity<?> getCommentsByTask(
      @PathVariable @Parameter(description = "Идентификатор задачи") Long taskId,
      Pageable pageable) {
    try {
      Page<CommentResponse> comments = commentService.getCommentsByTask(taskId, pageable);
      return ResponseEntity.ok(comments);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка");
    }
  }
}
