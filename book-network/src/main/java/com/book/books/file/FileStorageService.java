package com.book.books.file;

import com.book.books.book.Book;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    @Value("${spring.application.file.upload.photos-output-path}")
  private String fileUploadPath;

    public String saveFile(
            @Nonnull  MultipartFile sourceFile,
            @Nonnull String userId) {
        final String fileUploadSubPath = "users"+ separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
            @Nonnull  MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath) {


        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated) {
              log.warn("Failed to create folder {}", targetFolder.getAbsolutePath());
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // ./uploads/users/1/134334343343.jpg
        String targetFilePath = finalUploadPath + separator + System.currentTimeMillis()+"."+fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try
        {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to {}", targetFilePath);
            return targetFilePath;
        }catch(IOException e){
            log.error("File was not saved .",e );
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            return "";
        }
        // something.jpg
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex == -1){
            return "";
        }
        // .JPG  ->  .jpg
        return fileName.substring(lastDotIndex+ 1).toLowerCase();
    }


}
