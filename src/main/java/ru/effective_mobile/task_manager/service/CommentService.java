package ru.effective_mobile.task_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.effective_mobile.task_manager.dto.CommentRequest;
import ru.effective_mobile.task_manager.dto.CommentResponse;
import ru.effective_mobile.task_manager.entities.Comment;
import ru.effective_mobile.task_manager.entities.Task;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.CommentRepository;
import ru.effective_mobile.task_manager.repository.TaskRepository;
import ru.effective_mobile.task_manager.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * Сервис для работы с комментариями.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Создает новый комментарий.
     *
     * @param commentRequest Запрос на создание комментария.
     * @return Ответ с созданным комментарием.
     */
    public CommentResponse createComment(CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        newComment.setCreatedAt(LocalDateTime.now());

        Task task = taskRepository.findById(commentRequest.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        newComment.setTask(task);

        User author = userRepository.findById(commentRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        newComment.setAuthor(author);

        Comment savedComment = commentRepository.save(newComment);
        return mapToCommentResponse(savedComment);
    }

    /**
     * Получает список комментариев по идентификатору задачи с пагинацией.
     *
     * @param taskId   Идентификатор задачи.
     * @param pageable Объект пагинации.
     * @return Страница с комментариями.
     */
    public Page<CommentResponse> getCommentsByTask(Long taskId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByTaskId(taskId, pageable);
        return comments.map(this::mapToCommentResponse);
    }

    /**
     * Преобразует сущность Comment в DTO CommentResponse.
     *
     * @param comment Сущность Comment.
     * @return DTO CommentResponse.
     */
    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setTaskId(comment.getTask().getId());
        response.setAuthorId(comment.getAuthor().getId());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}