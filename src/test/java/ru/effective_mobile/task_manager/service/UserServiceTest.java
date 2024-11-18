package ru.effective_mobile.task_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effective_mobile.task_manager.dto.RegisterRequest;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.UserRepository;

class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRegisterUser() {
    RegisterRequest registerRequest =
        RegisterRequest.builder().email("user@example.com").password("password").build();

    when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(new User());

    userService.registerUser(registerRequest);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterUserWithExistingEmail() {
    RegisterRequest registerRequest =
        RegisterRequest.builder().email("user@example.com").password("password").build();

    when(userRepository.findByEmail(registerRequest.getEmail()))
        .thenReturn(Optional.of(new User()));

    assertThatThrownBy(() -> userService.registerUser(registerRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Email already exists");
  }

  @Test
  void testSaveRefreshToken() {
    String email = "user@example.com";
    String refreshToken = "refreshToken";

    User user = User.builder().email(email).password("password").role(User.Role.USER).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    userService.saveRefreshToken(email, refreshToken);

    verify(userRepository, times(1)).save(any(User.class));
    assertThat(user.getRefreshToken()).isEqualTo(refreshToken);
  }

  @Test
  void testSaveRefreshTokenWithInvalidEmail() {
    String email = "user@example.com";
    String refreshToken = "refreshToken";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.saveRefreshToken(email, refreshToken))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("User not found with email: " + email);
  }
}
