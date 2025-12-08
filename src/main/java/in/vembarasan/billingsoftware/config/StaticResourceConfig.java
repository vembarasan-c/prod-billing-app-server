package in.vembarasan.billingsoftware.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(StaticResourceConfig.class);

    @Value("${server.servlet.context-path:/api/v1.0}")
    private String contextPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use absolute path for uploads directory
        String uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toString();

        // Ensure the path ends with a separator
        if (!uploadDir.endsWith("/") && !uploadDir.endsWith("\\")) {
            uploadDir += "/";
        }

        // Register resource handler for uploads
        // Spring Boot automatically prepends the context path, so this will be accessible at:
        // {context-path}/uploads/** (e.g., /api/v1.0/uploads/**)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir)
                .setCachePeriod(3600); // Cache for 1 hour

        logger.info("Static resource handler configured for uploads at: file:{}", uploadDir);
        logger.info("Images will be accessible at: {}/uploads/**", contextPath);
    }
}
