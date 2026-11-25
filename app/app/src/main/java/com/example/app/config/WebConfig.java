package com.example.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1) exige login em tudo, menos nestas rotas
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/logout", "/error", "/css/**", "/js/**", "/img/**");

        // 2) estas rotas são só do administrador
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/questoes/**", "/avaliacoes/**", "/relatorios/**", "/usuarios/**");
    }
}