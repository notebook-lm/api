package vn.edu.fsoftacademy.api.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtAccessTokenAdapter accessTokens;

  public JwtAuthenticationFilter(JwtAccessTokenAdapter accessTokens) {
    this.accessTokens = accessTokens;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      if (accessTokens.isValid(token)) {
        var authentication = new UsernamePasswordAuthenticationToken(
            accessTokens.extractUserId(token),
            null,
            accessTokens.extractAuthorities(token).stream().map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    chain.doFilter(request, response);
  }
}
