package com.Auton.gibg.controller.imageUpload;

import com.Auton.gibg.entity.user.user_entity;
import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.myfuntions.ImageResizer;
import com.Auton.gibg.response.ResponseWrapper;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.Auton.gibg.response.ImageRespone.ImageUploadResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.Auton.gibg.middleware.authToken;

@Controller
@RequestMapping("/api/admin")
public class imageUpload_controller {
    private final authToken authService;
    @Value("${upload.dir}")
    private String uploadDir;
    @Value("${uploadFilePro.dir}")
    private String uploadProDir;

    @Autowired
    public imageUpload_controller(authToken authService) {
        this.authService = authService;
    }


//    @PostMapping("/user/profile/upload")
//    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("imageFile") MultipartFile imageFile) {
//        ImageUploadResponse response = new ImageUploadResponse();
//
//        if (imageFile.isEmpty()) {
//            response.setMessage("Image file is required");
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//        try {
//            // Generate a random name for the uploaded image
//            String fileExtension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
//            String randomFileName = UUID.randomUUID().toString() + "." + fileExtension;
//
//            // Check if the file extension is allowed (jpg, png, svg)
//            if (!fileExtension.matches("jpg|png|svg")) {
//                response.setMessage("Only JPG, PNG, and SVG files are allowed");
//                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//            }
//
//            // Construct the path to save the image within the project's directory
//            String filePath = ResourceUtils.getFile(uploadDir).getAbsolutePath() + File.separator + randomFileName;
//
//            // Create the upload directory if it doesn't exist
//            File uploadDirFile = new File(uploadDir);
//            if (!uploadDirFile.exists()) {
//                uploadDirFile.mkdirs();
//            }
//
//            // Save the uploaded image to the specified path
//            File destFile = new File(filePath);
//            imageFile.transferTo(destFile);
//
//            response.setMessage("Image uploaded successfully");
//            response.setUrlPath(filePath);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            response.setMessage("Error uploading image");
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/user/profile/upload")
    public ResponseEntity<ImageUploadResponse> uploadImageToCloudinary(@RequestParam("imageFile") MultipartFile imageFile, @RequestHeader("Authorization") String authorizationHeader) {
        ImageUploadResponse response = new ImageUploadResponse();

        try {
            if (imageFile.isEmpty()) {
                response.setMessage("Image file is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            // Check image size
            if (imageFile.getSize() > 1024 * 1024 * 9) { // 9MB
                response.setMessage("Image size must be less than 9MB");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Generate a random name for the uploaded image
            String fileExtension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
            String randomFileName = "profile_" + System.currentTimeMillis() + fileExtension;

            // Initialize Cloudinary instance
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "ds639zn4t", "api_key", "297389628948212", "api_secret", "gyTMSMSLKyKjVG6A_IC3bhrHUvE"));

            // Resize and upload image to Cloudinary
            Map<String, Object> resizeParams = ObjectUtils.asMap(
                    "transformation", new Transformation().width(600).height(600).crop("limit")
            );
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), resizeParams);

            // Get the Cloudinary URL of the uploaded image
            String imageUrl = (String) uploadResult.get("secure_url");

            response.setMessage("Image uploaded successfully");
            response.setUrlPath(imageUrl);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            response.setMessage("Error uploading image");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/user/profile/delete")
//    public ResponseEntity<ImageUploadResponse> deleteFile(@RequestParam("filePath") String filePath) {
//        ImageUploadResponse response = new ImageUploadResponse();
//
//        try {
//            Path path = Paths.get(filePath);
//
//            // Check if the file exists
//            if (Files.exists(path)) {
//                Files.delete(path);
//                response.setMessage("File deleted successfully");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                response.setMessage("File not found");
//                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            response.setMessage("Error deleting file");
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @DeleteMapping("/user/profile/delete")
    public ResponseEntity<String> deleteImageFromCloudinary(@RequestParam("imageUrl") String imageUrl) {
        try {
            // Initialize Cloudinary instance
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "ds639zn4t", "api_key", "297389628948212", "api_secret", "gyTMSMSLKyKjVG6A_IC3bhrHUvE"));

            // Extract the public ID from the Cloudinary URL
            String publicId = String.valueOf(cloudinary.url().publicId(imageUrl));

            // Delete image from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            return new ResponseEntity<>("Image deleted successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ---------------------------------------------------------------- any images for product
//    @PostMapping("/product/images/upload")
//    public ResponseEntity<ResponseWrapper<List<ImageUploadResponse>>> uploadMultipleImages(@RequestParam("imageFiles") List<MultipartFile> imageFiles) {
//        List<ImageUploadResponse> responses = new ArrayList<>();
//        if (imageFiles.size() > 15) {
//            ImageUploadResponse response = new ImageUploadResponse();
//            response.setMessage("You can upload a maximum of 15 images");
//            responses.add(response);
//            return ResponseEntity.badRequest().body(new ResponseWrapper<>("Maximum image upload limit exceeded", responses));
//        }
//
//        for (MultipartFile imageFile : imageFiles) {
//            ImageUploadResponse response = new ImageUploadResponse();
//
//            if (imageFile.isEmpty()) {
//                response.setMessage("Image file is required");
//                responses.add(response);
//                continue;
//            }
//
//            try {
//                // Generate a random name for the uploaded image
//                String fileExtension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
//                String randomFileName = UUID.randomUUID().toString() + "." + fileExtension;
//
//                // Check if the file extension is allowed (jpg, png, svg)
//                if (!fileExtension.matches("jpg|png|svg")) {
//                    response.setMessage("Only JPG, PNG, and SVG files are allowed");
//                    responses.add(response);
//                    continue;
//                }
//
//                // Construct the path to save the image within the project's directory
//                String filePath = ResourceUtils.getFile(uploadProDir).getAbsolutePath() + File.separator + randomFileName;
//
//                // Create the upload directory if it doesn't exist
//                File uploadDirFile = new File(uploadProDir);
//                if (!uploadDirFile.exists()) {
//                    uploadDirFile.mkdirs();
//                }
//
//                // Save the uploaded image to the specified path
//                File destFile = new File(filePath);
//                imageFile.transferTo(destFile);
//
//                response.setMessage("Image uploaded successfully");
//                response.setUrlPath(filePath);
//
//                responses.add(response);
//            } catch (IOException e) {
//                e.printStackTrace();
//                response.setMessage("Error uploading image");
//                responses.add(response);
//            }
//        }
//
//        return ResponseEntity.ok(new ResponseWrapper<>("Images uploaded successfully", responses));
//    }

    @PostMapping("/product/images/upload")
    public ResponseEntity<ResponseWrapper<List<ImageUploadResponse>>> uploadMultipleImagesToCloudinary(@RequestParam("imageFiles") List<MultipartFile> imageFiles) {
        List<ImageUploadResponse> responses = new ArrayList<>();
        if (imageFiles.size() > 15) {
            ImageUploadResponse response = new ImageUploadResponse();
            response.setMessage("You can upload a maximum of 15 images");
            responses.add(response);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>("Maximum image upload limit exceeded", responses));
        }

        for (MultipartFile imageFile : imageFiles) {
            ImageUploadResponse response = new ImageUploadResponse();

            if (imageFile.isEmpty()) {
                response.setMessage("Image file is required");
                responses.add(response);
                continue;
            }

            try {
                // Initialize Cloudinary instance
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "ds639zn4t", "api_key", "297389628948212", "api_secret", "gyTMSMSLKyKjVG6A_IC3bhrHUvE"));

                // Resize parameters for each image
                Transformation transformation = new Transformation().width(600).height(600).crop("limit");
                Map<String, Object> resizeParams = ObjectUtils.asMap(
                        "transformation", transformation
                );

                // Upload image to Cloudinary with resizing
                Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), resizeParams);

                // Get the Cloudinary URL of the uploaded image
                String imageUrl = (String) uploadResult.get("secure_url");

                response.setMessage("Image uploaded successfully");
                response.setUrlPath(imageUrl);

                responses.add(response);
            } catch (IOException e) {
                e.printStackTrace();
                response.setMessage("Error uploading image");
                responses.add(response);
            }
        }

        return ResponseEntity.ok(new ResponseWrapper<>("Images uploaded successfully", responses));
    }


    //----------------------------------------------------------------
    @DeleteMapping("/product/images/delete")
    public ResponseEntity<ResponseWrapper<String>> deleteImages(@RequestBody List<String> imagePaths) {
        List<String> deletedImages = new ArrayList<>();
        List<String> failedDeletions = new ArrayList<>();

        for (String imagePath : imagePaths) {
            try {
                File imageFile = new File(imagePath);

                if (imageFile.exists() && imageFile.delete()) {
                    deletedImages.add(imagePath);
                } else {
                    failedDeletions.add(imagePath);
                }
            } catch (Exception e) {
                failedDeletions.add(imagePath);
            }
        }

        String message = "Images deleted successfully";
        if (!deletedImages.isEmpty()) {
            message += " (" + deletedImages.size() + " images)";
        }

        if (!failedDeletions.isEmpty()) {
            message += ", " + failedDeletions.size() + " images failed to delete";
        }

        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(message, null);
        return ResponseEntity.ok(responseWrapper);
    }


    // icon
    @PostMapping("/icon/upload")
    public ResponseEntity<ImageUploadResponse> uploadIconToCloudinary(@RequestParam("iconFile") MultipartFile imageFile, @RequestHeader("Authorization") String authorizationHeader) {
        ImageUploadResponse response = new ImageUploadResponse();

        try {
            if (imageFile.isEmpty()) {
                response.setMessage("icon file is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            // Check image size
            if (imageFile.getSize() > 1024 * 1024 * 9) { // 9MB
                response.setMessage("icon size must be less than 9MB");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Generate a random name for the uploaded image
            String fileExtension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
            String randomFileName = "icon_" + System.currentTimeMillis() + fileExtension;

            // Initialize Cloudinary instance
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "ds639zn4t", "api_key", "297389628948212", "api_secret", "gyTMSMSLKyKjVG6A_IC3bhrHUvE"));

            // Resize and upload image to Cloudinary
            Map<String, Object> resizeParams = ObjectUtils.asMap(
                    "transformation", new Transformation().width(600).height(600).crop("limit")
            );
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), resizeParams);

            // Get the Cloudinary URL of the uploaded image
            String imageUrl = (String) uploadResult.get("secure_url");

            response.setMessage("icon uploaded successfully");
            response.setUrlPath(imageUrl);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            response.setMessage("Error uploading icon");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}



