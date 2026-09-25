package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.fsoftacademy.api.api.rest.shared.error.ApiErrorCode;
import vn.edu.fsoftacademy.api.infrastructure.security.config.ApiAuthenticationEntryPoint;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtAccessTokenAdapter accessTokens;
  private final ApiAuthenticationEntryPoint authenticationEntryPoint;

  public JwtAuthenticationFilter(
      JwtAccessTokenAdapter accessTokens, ApiAuthenticationEntryPoint authenticationEntryPoint) {
    this.accessTokens = accessTokens;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      var validationResult = accessTokens.validationResult(token);
      if (validationResult == JwtAccessTokenAdapter.TokenValidationResult.EXPIRED) {
        authenticationEntryPoint.write(
            response,
            HttpStatus.UNAUTHORIZED,
            ApiErrorCode.ACCESS_TOKEN_EXPIRED,
            "Access token has expired");
        return;
      }
      if (validationResult == JwtAccessTokenAdapter.TokenValidationResult.INVALID) {
        authenticationEntryPoint.write(
            response,
            HttpStatus.UNAUTHORIZED,
            ApiErrorCode.ACCESS_TOKEN_INVALID,
            "Access token is invalid");
        return;
      }
      if (validationResult == JwtAccessTokenAdapter.TokenValidationResult.VALID) {
        var authentication =
            new UsernamePasswordAuthenticationToken(
                accessTokens.extractUserId(token),
                null,
                accessTokens.extractAuthorities(token).stream()
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    chain.doFilter(request, response);
  }
}
