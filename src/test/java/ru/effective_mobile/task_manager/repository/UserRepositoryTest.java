package ru.effective_mobile.task_manager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.effective_mobile.task_manager.entities.User;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void testFindByEmail() {
    User user =
        User.builder().email("email@email.com").password("password").role(User.Role.ADMIN).build();

    userRepository.save(user);

    Optional<User> found = userRepository.findByEmail("email@email.com");
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isGreaterThan(0);
    assertThat(found.get().getEmail()).isEqualTo("email@email.com");
  }

  @Test
  void testFindByRefreshToken() {
    User user =
        User.builder()
            .email("email@email.com")
            .password("password")
            .refreshToken("refreshToken")
            .role(User.Role.ADMIN)
            .build();

    userRepository.save(user);

    Optional<User> found = userRepository.findByRefreshToken("refreshToken");
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isGreaterThan(0);
    assertThat(found.get().getEmail()).isEqualTo("email@email.com");
  }
}
