package com.lumi.app.service.impl;

import com.lumi.app.service.ChunkUploadService;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Service
public class ChunkUploadServiceImpl implements ChunkUploadService {

    private final Path tempPath;
    private final Path finalPath;

    public ChunkUploadServiceImpl(Path tempPath, Path finalPath) {
        this.tempPath = tempPath;
        this.finalPath = finalPath;
    }

    @Override
    public void saveChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException {
        Path dir = tempPath.resolve(uploadId);
        Files.createDirectories(dir);

        Path chunkPath = dir.resolve(String.valueOf(chunkIndex));
        Files.copy(file.getInputStream(), chunkPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public String mergeChunks(String uploadId, int totalChunks, String fileName) throws IOException {
        Path dir = tempPath.resolve(uploadId);
        Path finalFile = finalPath.resolve(fileName);

        try (OutputStream os = Files.newOutputStream(finalFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkPath = dir.resolve(String.valueOf(i));
                Files.copy(chunkPath, os);
            }
        }

        FileSystemUtils.deleteRecursively(dir); // cleanup temp
        return finalFile.toString();
    }
}
