package com.ginkgooai.core.ai.config;

import com.ginkgooai.core.common.interceptor.ContextsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebInterceptor implements WebMvcConfigurer {

    /**
     * Adds interceptors to the application request processing pipeline
     * 
     * @param registry The interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ContextsInterceptor()).addPathPatterns("/**");
    }

}
