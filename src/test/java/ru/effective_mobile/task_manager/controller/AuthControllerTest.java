package ru.effective_mobile.task_manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.effective_mobile.task_manager.dto.*;
import ru.effective_mobile.task_manager.exception.TokenRefreshException;
import ru.effective_mobile.task_manager.security.JwtRequestFilter;
import ru.effective_mobile.task_manager.service.RefreshTokenService;
import ru.effective_mobile.task_manager.service.UserDetailsServiceImpl;
import ru.effective_mobile.task_manager.service.UserService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthenticationManager authenticationManager;

  @MockBean private UserDetailsServiceImpl userDetailsService;

  @MockBean private UserService userService;

  @MockBean private RefreshTokenService refreshTokenService;

  @MockBean private JwtRequestFilter jwtRequestFilter;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
  }

  @Test
  public void testLoginSuccess() throws Exception {
    AuthRequest authRequest =
        AuthRequest.builder().email("test@example.com").password("password").build();
    UserDetails userDetails = mock(UserDetails.class);
    String jwtToken = "jwtToken";
    String refreshToken = "refreshToken";

    when(authenticationManager.authenticate(any())).thenReturn(null);
    when(userDetailsService.loadUserByUsername(authRequest.getEmail())).thenReturn(userDetails);
    when(jwtRequestFilter.generateToken(userDetails)).thenReturn(jwtToken);
    when(refreshTokenService.createRefreshToken(authRequest.getEmail())).thenReturn(refreshToken);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(jwtToken))
        .andExpect(jsonPath("$.refreshToken").value(refreshToken));
  }

  @Test
  public void testLoginFailure() throws Exception {
    AuthRequest authRequest =
        AuthRequest.builder().email("test@example.com").password("wrongPassword").build();

    when(authenticationManager.authenticate(any()))
        .thenThrow(new RuntimeException("Неверный email или пароль"));

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Неверный email или пароль"));
  }

  @Test
  public void testRegisterSuccess() throws Exception {
    RegisterRequest registerRequest =
        RegisterRequest.builder().email("test@example.com").password("password").build();

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("Пользователь успешно зарегистрирован!"));
  }

  @Test
  public void testRegisterFailure() throws Exception {
    RegisterRequest registerRequest =
        RegisterRequest.builder().email("test@example.com").password("password").build();

    doThrow(new RuntimeException("Ошибка регистрации")).when(userService).registerUser(any());

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Ошибка регистрации"));
  }

  @Test
  public void testRefreshTokenFailure() throws Exception {
    RefreshTokenRequest refreshTokenRequest =
        RefreshTokenRequest.builder().refreshToken("invalidRefreshToken").build();

    when(refreshTokenService.refreshAccessToken(refreshTokenRequest.getRefreshToken()))
        .thenThrow(
            new TokenRefreshException(
                "invalidRefreshToken", "Неверный или просроченный refresh токен"));

    mockMvc
        .perform(
            post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isForbidden());
  }
}
