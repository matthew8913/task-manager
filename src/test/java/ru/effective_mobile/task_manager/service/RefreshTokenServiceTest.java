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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.UserRepository;
import ru.effective_mobile.task_manager.security.JwtRequestFilter;

class RefreshTokenServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private JwtRequestFilter jwtRequestFilter;

  @Mock private UserDetailsService userDetailsService;

  @InjectMocks private RefreshTokenService refreshTokenService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateRefreshToken() {
    String email = "user@example.com";
    User user = User.builder().email(email).password("password").role(User.Role.USER).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    String refreshToken = refreshTokenService.createRefreshToken(email);

    assertThat(refreshToken).isNotNull();
    verify(userRepository, times(1)).save(any(User.class));
    assertThat(user.getRefreshToken()).isEqualTo(refreshToken);
  }

  @Test
  void testCreateRefreshTokenWithInvalidEmail() {
    String email = "user@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> refreshTokenService.createRefreshToken(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("User not found with email: " + email);
  }

  @Test
  void testRefreshAccessToken() {
    String refreshToken = "refreshToken";
    String email = "user@example.com";
    User user =
        User.builder()
            .email(email)
            .password("password")
            .role(User.Role.USER)
            .refreshToken(refreshToken)
            .build();

    UserDetails userDetails = mock(UserDetails.class);
    String newAccessToken = "newAccessToken";

    when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(user));
    when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

    String accessToken = refreshTokenService.refreshAccessToken(refreshToken);

    assertThat(accessToken).isNotEqualTo(newAccessToken);
  }

  @Test
  void testDeleteRefreshToken() {
    String email = "user@example.com";
    User user =
        User.builder()
            .email(email)
            .password("password")
            .role(User.Role.USER)
            .refreshToken("refreshToken")
            .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    refreshTokenService.deleteRefreshToken(email);

    verify(userRepository, times(1)).save(any(User.class));
    assertThat(user.getRefreshToken()).isNull();
  }

  @Test
  void testDeleteRefreshTokenWithInvalidEmail() {
    String email = "user@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> refreshTokenService.deleteRefreshToken(email))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("User not found with email: " + email);
  }
}
