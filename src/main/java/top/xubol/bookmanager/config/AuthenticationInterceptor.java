package top.xubol.bookmanager.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.xubol.bookmanager.common.RequireRole;
import top.xubol.bookmanager.util.JwtUtils;

import java.lang.reflect.Method;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过 (例如静态资源)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查是否有RequireRole注解
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
        RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);

        // 如果方法和类上都没有注解，则放行
        if (methodAnnotation == null && classAnnotation == null) {
            return true;
        }

        String requiredRole = methodAnnotation != null ? methodAnnotation.value() : classAnnotation.value();

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        token = token.substring(7);
        try {
            // 验证token并获取role
            String username = jwtUtils.extractUsername(token);
            if (username == null || !jwtUtils.validateToken(token, username)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            
            // 权限校验
            // 约定: "admin" 对应 role=1, "user" 对应 role=0 或 1 (只要登录即可)
            if ("admin".equals(requiredRole)) {
                Integer roleEncoded = jwtUtils.extractRole(token);
                if (roleEncoded == null || roleEncoded != 1) {
                     response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                     return false;
                }
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
