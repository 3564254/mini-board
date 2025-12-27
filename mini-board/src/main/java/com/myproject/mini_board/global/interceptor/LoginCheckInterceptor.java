package com.myproject.mini_board.global.interceptor;

import com.myproject.mini_board.domain.user.User;
import com.myproject.mini_board.global.utils.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object loginUser = request.getSession().getAttribute(SessionConst.LOGIN_USER_ID);
        if (loginUser == null) {
            response.sendRedirect("/users/login");
            return false;
        }
        return true;
    }
}
