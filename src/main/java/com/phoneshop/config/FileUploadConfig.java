package com.phoneshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
@Configuration
public class FileUploadConfig  implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imagesPath = Paths.get("src/main/resources/static/images")
                .toAbsolutePath()
                .toUri()
                .toString();

        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagesPath);
    }
}
