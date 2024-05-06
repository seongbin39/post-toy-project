package store.sbin.postservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        // 파일 업로드 경로
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/uploads/");

        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
