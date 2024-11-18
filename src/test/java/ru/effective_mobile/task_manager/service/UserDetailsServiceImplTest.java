package ru.effective_mobile.task_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.UserRepository;

class UserDetailsServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testLoadUserByUsername() {
    String email = "user@example.com";
    User user = User.builder().email(email).password("password").role(User.Role.USER).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(email);
    assertThat(userDetails.getPassword()).isEqualTo("password");
    assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
  }

  @Test
  void testLoadUserByUsernameWithInvalidEmail() {
    String email = "user@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("User not found with email: " + email);
  }
}
