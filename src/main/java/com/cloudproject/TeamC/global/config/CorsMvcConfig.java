package com.cloudproject.TeamC.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*") // 허용할 HTTP 메서드
                .allowedHeaders("*"); // 모든 헤더 허용
        //  .allowCredentials(true);
    }
}