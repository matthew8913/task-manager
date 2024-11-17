package ru.effective_mobile.task_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective_mobile.task_manager.dto.*;
import ru.effective_mobile.task_manager.exception.TokenRefreshException;
import ru.effective_mobile.task_manager.security.JwtRequestFilter;
import ru.effective_mobile.task_manager.service.RefreshTokenService;
import ru.effective_mobile.task_manager.service.UserDetailsServiceImpl;
import ru.effective_mobile.task_manager.service.UserService;

@Tag(name = "Аутентификация", description = "Эндпоинты для аутентификации и регистрации пользователей")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtRequestFilter jwtRequestFilter;

    @Operation(summary = "Аутентификация пользователя и генерация JWT токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аутентификация успешна",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный email или пароль",
                    content = @Content)})
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Parameter(description = "Данные для аутентификации") AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Неверный email или пароль", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtRequestFilter.generateToken(userDetails);
        final String refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());
        userService.saveRefreshToken(authRequest.getEmail(), refreshToken);
        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
    }

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка регистрации",
                    content = @Content)})
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Parameter(description = "Данные для регистрации") RegisterRequest registrationRequest) {
        try {
            userService.registerUser(registrationRequest);
            return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Обновление access токена с использованием refresh токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Неверный или просроченный refresh токен",
                    content = @Content)})
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Parameter(description = "Запрос на обновление токена") RefreshTokenRequest refreshTokenRequest) {
        try {
            String newAccessToken = refreshTokenService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Выход пользователя и аннулирование refresh токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Выход успешный",
                    content = @Content)})
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody @Parameter(description = "Запрос на выход") LogoutRequest logoutRequest) {
        refreshTokenService.deleteRefreshToken(logoutRequest.getEmail());
        return ResponseEntity.ok("Выход успешный");
    }
}