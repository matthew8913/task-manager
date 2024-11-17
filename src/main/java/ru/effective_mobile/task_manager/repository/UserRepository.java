package ru.effective_mobile.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.effective_mobile.task_manager.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByRefreshToken(String refreshToken);
}
