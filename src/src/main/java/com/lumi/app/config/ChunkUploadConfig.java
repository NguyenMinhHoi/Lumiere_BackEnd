package com.lumi.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ChunkUploadConfig {

    @Value("${app.upload.temp-dir}")
    private String tempDir;

    @Value("${app.upload.final-dir}")
    private String finalDir;

    @Bean
    public Path tempUploadPath() throws IOException {
        Path path = Paths.get(tempDir).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path;
    }

    @Bean
    public Path finalUploadPath() throws IOException {
        Path path = Paths.get(finalDir).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path;
    }
}
