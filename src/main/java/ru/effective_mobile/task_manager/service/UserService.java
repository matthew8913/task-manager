package ru.effective_mobile.task_manager.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effective_mobile.task_manager.dto.RegisterRequest;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void registerUser(RegisterRequest registrationRequest) {
    if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email already exists");
    }

    User user = new User();
    user.setEmail(registrationRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
    user.setRole(User.Role.USER);

    userRepository.save(user);
  }

  @PostConstruct
  public void init() {
    if (userRepository.findByEmail("admin@example.com").isEmpty()) {
      User admin = new User();
      admin.setEmail("admin@example.com");
      admin.setPassword(passwordEncoder.encode("adminPassword"));
      admin.setRole(User.Role.ADMIN);
      userRepository.save(admin);
    }
  }

  public void saveRefreshToken(String email, String refreshToken) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));
    user.setRefreshToken(refreshToken);
    userRepository.save(user);
  }
}
