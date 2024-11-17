package ru.effective_mobile.task_manager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Фильтр для обработки JWT в запросах. Проверяет наличие и валидность JWT, а также устанавливает
 * аутентификацию в контекст безопасности.
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

  private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final UserDetailsService userDetailsService;

  /**
   * Основной метод фильтрации запросов. Извлекает JWT из заголовка, проверяет его валидность и
   * устанавливает аутентификацию в контекст безопасности.
   *
   * @param request HTTP-запрос
   * @param response HTTP-ответ
   * @param chain цепочка фильтров
   * @throws ServletException если произошла ошибка сервлета
   * @throws IOException если произошла ошибка ввода-вывода
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    final String authorizationHeader = request.getHeader("Authorization");

    String email = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      email = extractEmail(jwt);
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

      if (validateToken(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    chain.doFilter(request, response);
  }

  /**
   * Извлекает адрес электронной почты из токена.
   *
   * @param token JWT-токен
   * @return адрес электронной почты
   */
  private String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Извлекает указанный клейм из токена.
   *
   * @param token JWT-токен
   * @param claimsResolver функция для извлечения клейма
   * @param <T> тип клейма
   * @return значение клейма
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Извлекает все клеймы из JWT-токена.
   *
   * @param token JWT-токен
   * @return объект Claims с клеймами
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
  }

  /**
   * Проверяет валидность токена на основе адреса электронной почты и срока действия.
   *
   * @param token JWT-токен
   * @param userDetails детали пользователя
   * @return true, если токен валиден; иначе false
   */
  private Boolean validateToken(String token, UserDetails userDetails) {
    final String email = extractEmail(token);
    return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Проверяет, истек ли срок действия токена.
   *
   * @param token JWT-токен
   * @return true, если срок действия истек; иначе false
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Извлекает дату истечения срока действия токена.
   *
   * @param token JWT-токен
   * @return дата истечения срока действия
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Генерирует новый JWT-токен для указанного пользователя.
   *
   * @param userDetails детали пользователя
   * @return сгенерированный JWT-токен
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  /**
   * Создает новый JWT-токен с указанными клеймами и субъектом.
   *
   * @param claims карта с клеймами
   * @param subject субъект токена (например, адрес электронной почты)
   * @return сгенерированный JWT-токен
   */
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 часов
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }
}
