package com.cropMatch.security;

import com.cropMatch.model.UserPrincipal;
import com.cropMatch.repository.UserDetailRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final UserDetailRepository userDetailRepository;

    @Autowired
    private final TokenBlacklist tokenBlacklist;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for login/register endpoints
        String path = request.getServletPath();
        if ("/login".equals(path) || "/register".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = extractJwtToken(request);

        try {
            if (jwtToken != null && jwtUtil.validateToken(jwtToken) && !tokenBlacklist.isBlacklisted(jwtToken)) {
                String username = jwtUtil.extractUsername(jwtToken);
                authenticateUser(username, request);
            }
        } catch (Exception e) {
            // Clear context but don't redirect - let the request proceed
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void authenticateUser(String username, HttpServletRequest request) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userDetailRepository.findByUsername(username).ifPresent(user -> {
                String role = user.getUserTypes().iterator().next().getUserType().getName();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(user),
                        null,
                        Collections.singleton(new SimpleGrantedAuthority(role))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
        response.sendRedirect("/login?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
