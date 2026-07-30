package com.lsy.propertymanagementsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源直接处理
        registry.addResourceHandler("/assets/**", "/favicon.svg")
                .addResourceLocations("classpath:/static/");

        // SPA fallback: 非API、非静态资源请求返回 index.html
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        // API 路径不返回 index.html，交由 Spring MVC 处理（返回 404 或由控制器处理）
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        // SPA fallback: 其他路径返回 index.html，由 Vue Router 处理
                        Resource indexHtml = location.createRelative("index.html");
                        if (indexHtml.exists() && indexHtml.isReadable()) {
                            return indexHtml;
                        }
                        return null;
                    }
                });
    }
}