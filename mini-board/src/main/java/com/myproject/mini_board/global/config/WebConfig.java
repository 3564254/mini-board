package com.myproject.mini_board.global.config;

import com.myproject.mini_board.global.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .order(1) // 인터셉터 실행 순서 지정
                .addPathPatterns("/**") // 모든 요청에 대해 인터셉터 적용
                .excludePathPatterns(
                        "/", "/posts", "/posts/{postId}", "/posts/search", "/users/register", "/users/login", "/users/logout",
                        "/css/**", "/*.ico", "/error", "/js/**", "/images/**"
                ); // 특정 경로는 인터셉터 적용에서 제외
    }
}
