package com.wanglei.zhixing.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.common.ResultCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录校验：/admin/** 要 admin 角色，/student/** 要 student 角色。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            String auth = request.getHeader("Authorization");
            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                token = auth.substring(7);
            }
        }
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response, "请先登录");
            return false;
        }

        try {
            Claims claims = jwtUtil.parse(token);
            String role = claims.get("role", String.class);
            String uri = request.getRequestURI();
            if (uri.startsWith("/admin/") && !"admin".equals(role)) {
                writeUnauthorized(response, "请使用后台账号登录");
                return false;
            }
            if (uri.startsWith("/student/") && !"student".equals(role)) {
                writeUnauthorized(response, "请使用学员账号登录");
                return false;
            }
            UserContext.set(Long.valueOf(claims.getSubject()), claims.get("name", String.class));
            return true;
        } catch (Exception e) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(ResultCode.UNAUTHORIZED.getCode(), msg)));
    }
}
