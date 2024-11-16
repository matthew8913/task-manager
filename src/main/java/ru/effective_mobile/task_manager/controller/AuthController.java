package ru.effective_mobile.task_manager.controller;

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
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.exception.TokenRefreshException;
import ru.effective_mobile.task_manager.security.JwtRequestFilter;
import ru.effective_mobile.task_manager.service.RefreshTokenService;
import ru.effective_mobile.task_manager.service.UserDetailsServiceImpl;
import ru.effective_mobile.task_manager.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtRequestFilter jwtRequestFilter;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect email or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtRequestFilter.generateToken(userDetails);
        final String refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());
        userService.saveRefreshToken(authRequest.getEmail(),refreshToken );
        return ResponseEntity.ok(new AuthResponse(jwt,refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registrationRequest) {
        try {
            User registeredUser = userService.registerUser(registrationRequest);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String newAccessToken = refreshTokenService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LogoutRequest logoutRequest) {
        refreshTokenService.deleteRefreshToken(logoutRequest.getEmail());
        return ResponseEntity.ok("Logout successful");
    }
}