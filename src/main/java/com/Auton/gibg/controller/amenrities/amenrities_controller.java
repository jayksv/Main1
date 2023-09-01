package com.Auton.gibg.controller.amenrities;

import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.repository.amenrities.amenrities_repository;
import com.Auton.gibg.repository.category.category_repository;
import com.Auton.gibg.entity.amenrities.amenrities_entity;
import com.Auton.gibg.response.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/admin")
public class amenrities_controller {
    private final amenrities_repository amenritiesRepository;
    private final authToken authService;

    @Autowired
    public amenrities_controller(amenrities_repository amenritiesRepository, authToken authService) {
        this.amenritiesRepository = amenritiesRepository;
        this.authService = authService;
    }

    @PostMapping("/amenrities/insert")
    public ResponseEntity<ResponseWrapper<String>> insertAmenrities(
            @RequestBody amenrities_entity amenrities,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            // Check if required fields are not null
            if (amenrities.getAmenities_name() == null) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Amenities name cannot be null.", null);
                return ResponseEntity.badRequest().body(responseWrapper);
            }

            // Insert amenities_entity using JpaRepository
            amenritiesRepository.save(amenrities);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Amenities inserted successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while inserting the amenities", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @GetMapping("/amenrities/all")
    public ResponseEntity<ResponseWrapper<List<amenrities_entity>>> getAllAmenrities(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            // Retrieve all amenities using JpaRepository
            List<amenrities_entity> allAmenrities = amenritiesRepository.findAll();

            ResponseWrapper<List<amenrities_entity>> responseWrapper = new ResponseWrapper<>("Success", allAmenrities);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<List<amenrities_entity>> responseWrapper = new ResponseWrapper<>("An error occurred while retrieving amenities", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @GetMapping("/amenrities/findById/{amenitiesId}")
    public ResponseEntity<ResponseWrapper<amenrities_entity>> getAmenritiesById(
            @PathVariable Long amenitiesId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            // Retrieve amenrities by ID using JpaRepository
            Optional<amenrities_entity> amenitiesOptional = amenritiesRepository.findById(amenitiesId);
            if (amenitiesOptional.isEmpty()) {
                ResponseWrapper<amenrities_entity> notFoundResponse = new ResponseWrapper<>("Amenities not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            amenrities_entity amenrities = amenitiesOptional.get();
            ResponseWrapper<amenrities_entity> responseWrapper = new ResponseWrapper<>("Success", amenrities);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<amenrities_entity> responseWrapper = new ResponseWrapper<>("An error occurred while retrieving amenities", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }
    @PutMapping("/amenrities/update/{amenitiesId}")
    public ResponseEntity<ResponseWrapper<String>> updateAmenrities(
            @PathVariable Long amenitiesId,
            @RequestBody amenrities_entity updatedAmenrities,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            // Check if the amenrities exists
            Optional<amenrities_entity> existingAmenritiesOptional = amenritiesRepository.findById(amenitiesId);
            if (existingAmenritiesOptional.isEmpty()) {
                ResponseWrapper<String> notFoundResponse = new ResponseWrapper<>("Amenities not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            // Update the amenrities fields
            amenrities_entity existingAmenrities = existingAmenritiesOptional.get();
            existingAmenrities.setAmenities_name(updatedAmenrities.getAmenities_name());
            existingAmenrities.setAmenities_icon(updatedAmenrities.getAmenities_icon());

            // Save the updated amenrities
            amenritiesRepository.save(existingAmenrities);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Amenities updated successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while updating amenities", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }


}
