package ru.effective_mobile.task_manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.effective_mobile.task_manager.entities.Comment;

/** Репозиторий для работы с комментариями. */
public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findByTaskId(Long taskId, Pageable pageable);
}
