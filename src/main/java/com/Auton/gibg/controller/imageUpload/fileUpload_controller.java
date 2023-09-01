package com.Auton.gibg.controller.imageUpload;

import com.Auton.gibg.response.ImageRespone.ImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/api/admin")
public class fileUpload_controller {
    @Value("${uploadFile.dir}")
    private String uploadFile;

    @PostMapping("/user/file/upload")
    public ResponseEntity<ImageUploadResponse> uploadFile(@RequestParam("File") MultipartFile imageFile) {
        ImageUploadResponse response = new ImageUploadResponse();

        if (imageFile.isEmpty()) {
            response.setMessage(" file is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Generate a random name for the uploaded image
            String fileExtension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            String randomFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // Check if the file extension is allowed (jpg, png, svg)
            if (!fileExtension.matches("jpg|png|svg|pdf")) {
                response.setMessage("Only JPG, PNG, and SVG files are allowed");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Construct the path to save the image within the project's directory
            String filePath = ResourceUtils.getFile(uploadFile).getAbsolutePath() + File.separator + randomFileName;

            // Create the upload directory if it doesn't exist
            File uploadDirFile = new File(uploadFile);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Save the uploaded image to the specified path
            File destFile = new File(filePath);
            imageFile.transferTo(destFile);

            response.setMessage("Image uploaded successfully");
            response.setUrlPath(filePath);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            response.setMessage("Error uploading image");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/file/delete")
    public ResponseEntity<ImageUploadResponse> deleteFile(@RequestParam("filePath") String filePath) {
        ImageUploadResponse response = new ImageUploadResponse();

        try {
            Path path = Paths.get(filePath);

            // Check if the file exists
            if (Files.exists(path)) {
                Files.delete(path);
                response.setMessage("File deleted successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("File not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setMessage("Error deleting file");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
