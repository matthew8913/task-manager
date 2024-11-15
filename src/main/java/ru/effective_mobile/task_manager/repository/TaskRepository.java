package ru.effective_mobile.task_manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.effective_mobile.task_manager.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
}
