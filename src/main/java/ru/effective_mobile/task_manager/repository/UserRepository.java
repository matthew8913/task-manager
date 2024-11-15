package ru.effective_mobile.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.effective_mobile.task_manager.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
