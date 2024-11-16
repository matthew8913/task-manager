package ru.effective_mobile.task_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.exception.TokenRefreshException;
import ru.effective_mobile.task_manager.repository.UserRepository;
import ru.effective_mobile.task_manager.security.JwtRequestFilter;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final JwtRequestFilter jwtRequestFilter;
    private final UserDetailsService userDetailsService;

    public String createRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return refreshToken;
    }

    public String refreshAccessToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));

        if (user.getRefreshToken().equals(refreshToken)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            return jwtRequestFilter.generateToken(userDetails);
        } else {
            throw new TokenRefreshException(refreshToken, "Refresh token is not valid!");
        }
    }

    public void deleteRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}