package com.ski.ticketing.filter;

import com.ski.ticketing.properties.JwtProperties;
import com.ski.ticketing.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(jwtProperties.getAdminTokenName());

        if (token != null && !token.isEmpty()) {
            try {
                if (JwtUtil.validateToken(jwtProperties.getAdminSecretKey(), token)) {
                    Long userId = JwtUtil.getUserIdFromToken(jwtProperties.getAdminSecretKey(), token);
                    Long employeeId = JwtUtil.getEmpIdFromToken(jwtProperties.getAdminSecretKey(), token);
                    Long principalId = userId != null ? userId : employeeId;
                    String role = employeeId != null ? "ROLE_ADMIN" : "ROLE_USER";
                    if (principalId != null) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                principalId, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/admin/employee/login") ||
                path.startsWith("/user/user/login") ||
                path.startsWith("/user/user/register") ||
                path.startsWith("/user/announcement") ||
                path.startsWith("/user/course/list") ||
                path.startsWith("/user/equipment/list") ||
                path.startsWith("/user/ski-run") ||
                path.startsWith("/user/ticket/list") ||
                path.startsWith("/user/weather") ||
                path.startsWith("/user/resort") ||
                path.startsWith("/user/coach") ||
                path.startsWith("/user/facility") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}
