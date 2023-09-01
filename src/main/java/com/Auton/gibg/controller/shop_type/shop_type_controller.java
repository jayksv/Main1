package com.Auton.gibg.controller.shop_type;

import com.Auton.gibg.entity.user.user_entity;
import com.Auton.gibg.response.ResponseWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.Auton.gibg.repository.shop_type.shop_type_repository;
import com.Auton.gibg.entity.shop_type.shop_type_entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class shop_type_controller {
    @Value("${jwt_secret}")
    private String jwt_secret;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private  shop_type_repository shopTypeRepository;
    public shop_type_controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/shop_type/insert")
    public ResponseEntity<ResponseWrapper<String>> insertShopType(
            @RequestBody shop_type_entity shopType,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate the authorization header and verify the token as before
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Verify the token from the Authorization header
            String token = authorizationHeader.substring("Bearer ".length());

            Claims claims = Jwts.parser()
                    .setSigningKey(jwt_secret) // Replace with your secret key
                    .parseClaimsJws(token)
                    .getBody();

            // Check token expiration
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long userId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }
            // Check if the shopType object is null
            if (shopType == null) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Shop type data is missing.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Continue with inserting the shop type
            // Validate shop type data as needed
            // You can add validation logic here

            // Save the shop type entity to the repository
            shop_type_entity savedShopType = shopTypeRepository.save(shopType);

            if (savedShopType != null) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Shop type inserted successfully.", null);
                return ResponseEntity.status(HttpStatus.CREATED).body(responseWrapper);
            } else {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Failed to insert shop type.", null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
            }
        } catch (JwtException e) {
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while inserting shop type.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }


    @PutMapping("/shop_type/update/{shopTypeId}")
    public ResponseEntity<ResponseWrapper<String>> updateShopType(
            @PathVariable Long shopTypeId,
            @RequestBody shop_type_entity shopType,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate the authorization header and verify the token as before
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Verify the token from the Authorization header
            String token = authorizationHeader.substring("Bearer ".length());

            Claims claims = Jwts.parser()
                    .setSigningKey(jwt_secret) // Replace with your secret key
                    .parseClaimsJws(token)
                    .getBody();

            // Check token expiration
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long userId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            // Find the existing shop type by ID
            Optional<shop_type_entity> existingShopTypeOptional = shopTypeRepository.findById(shopTypeId);

            if (existingShopTypeOptional.isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Shop type not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Continue with updating the shop type
            shop_type_entity existingShopType = existingShopTypeOptional.get();

            // Update the properties of existingShopType with values from shopType
            existingShopType.setType_name(shopType.getType_name());
            // Update other properties as needed

            // Save the updated shop type entity
            shop_type_entity updatedShopType = shopTypeRepository.save(existingShopType);

            if (updatedShopType != null) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Shop type updated successfully.", null);
                return ResponseEntity.ok(responseWrapper);
            } else {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Failed to update shop type.", null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
            }
        } catch (JwtException e) {
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while updating shop type.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @DeleteMapping("/shop_type/delete/{shopTypeId}")
    public ResponseEntity<ResponseWrapper<String>> deleteShopType(
            @PathVariable Long shopTypeId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate the authorization header and verify the token as before
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Verify the token from the Authorization header
            String token = authorizationHeader.substring("Bearer ".length());

            Claims claims = Jwts.parser()
                    .setSigningKey(jwt_secret) // Replace with your secret key
                    .parseClaimsJws(token)
                    .getBody();

            // Check token expiration
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long userId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            // Find the existing shop type by ID
            Optional<shop_type_entity> existingShopTypeOptional = shopTypeRepository.findById(shopTypeId);

            if (existingShopTypeOptional.isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Shop type not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Continue with deleting the shop type
            shop_type_entity shopTypeToDelete = existingShopTypeOptional.get();

            // Delete the shop type entity
            shopTypeRepository.delete(shopTypeToDelete);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Shop type deleted successfully.", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (JwtException e) {
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while deleting shop type.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }
    @GetMapping("/shop_type/all")
    public ResponseEntity<ResponseWrapper<List<shop_type_entity>>> findAllShopTypes(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate the authorization header and verify the token as before

            // Fetch all shop types from the repository
            List<shop_type_entity> shopTypes = shopTypeRepository.findAll();

            ResponseWrapper<List<shop_type_entity>> responseWrapper = new ResponseWrapper<>("Shop types retrieved successfully.", shopTypes);
            return ResponseEntity.ok(responseWrapper);
        } catch (JwtException e) {
            ResponseWrapper<List<shop_type_entity>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<List<shop_type_entity>> responseWrapper = new ResponseWrapper<>("An error occurred while retrieving shop types.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }
    @GetMapping("/shop_type/{shopTypeId}")
    public ResponseEntity<ResponseWrapper<shop_type_entity>> findShopTypeById(
            @PathVariable Long shopTypeId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate the authorization header and verify the token as before

            // Find the shop type by ID
            Optional<shop_type_entity> shopTypeOptional = shopTypeRepository.findById(shopTypeId);

            if (shopTypeOptional.isEmpty()) {
                ResponseWrapper<shop_type_entity> responseWrapper = new ResponseWrapper<>("Shop type not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            shop_type_entity shopType = shopTypeOptional.get();
            ResponseWrapper<shop_type_entity> responseWrapper = new ResponseWrapper<>("Shop type retrieved successfully.", shopType);
            return ResponseEntity.ok(responseWrapper);
        } catch (JwtException e) {
            ResponseWrapper<shop_type_entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<shop_type_entity> responseWrapper = new ResponseWrapper<>("An error occurred while retrieving the shop type.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }


}
