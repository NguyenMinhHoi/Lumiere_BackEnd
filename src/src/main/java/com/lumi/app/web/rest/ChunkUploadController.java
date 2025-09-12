package com.lumi.app.web.rest;

import com.lumi.app.service.ChunkUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chunk-upload")
public class ChunkUploadController {

    private final ChunkUploadService service;

    public ChunkUploadController(ChunkUploadService service) {
        this.service = service;
    }

    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Upload-Id") String uploadId,
            @RequestHeader("Chunk-Index") int chunkIndex) throws IOException {
        service.saveChunk(uploadId, chunkIndex, file);
        return ResponseEntity.ok("Uploaded chunk " + chunkIndex);
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, String>> completeUpload(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName) throws IOException {

        String finalPath = service.mergeChunks(uploadId, totalChunks, fileName);

        Map<String, String> result = new HashMap<>();
        result.put("filePath", finalPath);
        return ResponseEntity.ok(result);
    }
}
