package com.lumi.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChunkUploadService {
    void saveChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException;

    String mergeChunks(String uploadId, int totalChunks, String fileName) throws IOException;
}
