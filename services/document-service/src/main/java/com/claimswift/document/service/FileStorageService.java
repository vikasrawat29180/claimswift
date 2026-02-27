package com.claimswift.document.service;

import com.claimswift.document.config.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(FileStorageProperties properties) throws IOException {
        this.fileStorageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath().normalize();

        Files.createDirectories(this.fileStorageLocation);
    }

    public String storeFile(MultipartFile file) throws IOException {

        String originalFileName = Path.of(file.getOriginalFilename())
                .getFileName().toString();

        String fileName = System.currentTimeMillis() + "_" + originalFileName;

        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }

    public Path loadFile(String filePath) {
        return Paths.get(filePath).toAbsolutePath().normalize();
    }
}