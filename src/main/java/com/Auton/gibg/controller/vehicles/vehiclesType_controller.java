package com.Auton.gibg.controller.vehicles;

import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.repository.vehicles.*;
import com.Auton.gibg.entity.vehicle.*;
import com.Auton.gibg.response.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/vehicles")
public class vehiclesType_controller {
    private final authToken authService;
    private final vehiclesType_repository vehiclesTypeRepository;

    @Autowired
    public vehiclesType_controller(vehiclesType_repository vehiclesTypeRepository , authToken authService) {
        this.vehiclesTypeRepository = vehiclesTypeRepository;
        this.authService = authService;
    }


    @PostMapping("/types/insert")
    public ResponseEntity<ResponseWrapper<String>> insertVehicleType(
            @RequestBody vechcleType_Entity vehicleTypeEntity,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                // Token is invalid or has expired
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if vehicle type name is provided
            if (vehicleTypeEntity.getVehicle_name() == null || vehicleTypeEntity.getVehicle_name().isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle type name is required.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Save the vehicle type using the repository
            vehiclesTypeRepository.save(vehicleTypeEntity);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle type added successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while adding the vehicle type", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @PutMapping("/types/update/{id}")
    public ResponseEntity<ResponseWrapper<String>> updateVehicleType(
            @PathVariable Long id,
            @RequestBody vechcleType_Entity updatedTypeEntity,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if the provided ID exists
            Optional<vechcleType_Entity> existingTypeOptional = vehiclesTypeRepository.findById(id);
            if (existingTypeOptional.isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle type not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            vechcleType_Entity existingTypeEntity = existingTypeOptional.get();

            // Update the fields of the existing entity with the new values
            existingTypeEntity.setVehicle_name(updatedTypeEntity.getVehicle_name());

            // Save the updated entity
            vehiclesTypeRepository.save(existingTypeEntity);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle type updated successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while updating the vehicle type", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @GetMapping("/types/findById/{id}")
    public ResponseEntity<ResponseWrapper<vechcleType_Entity>> getVehicleTypeById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<vechcleType_Entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Find the vehicle type by ID
            Optional<vechcleType_Entity> typeOptional = vehiclesTypeRepository.findById(id);
            if (typeOptional.isEmpty()) {
                ResponseWrapper<vechcleType_Entity> responseWrapper = new ResponseWrapper<>("Vehicle type not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            vechcleType_Entity foundType = typeOptional.get();
            ResponseWrapper<vechcleType_Entity> responseWrapper = new ResponseWrapper<>("Vehicle type found.", foundType);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<vechcleType_Entity> responseWrapper = new ResponseWrapper<>("An error occurred while fetching the vehicle type", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @DeleteMapping("/types/delete/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteVehicleTypeById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if the vehicle type exists
            if (!vehiclesTypeRepository.existsById(id)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle type not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Delete the vehicle type by ID
            vehiclesTypeRepository.deleteById(id);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle type deleted successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while deleting the vehicle type", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }
    @GetMapping("/types/all")
    public ResponseEntity<ResponseWrapper<List<vechcleType_Entity>>> getAllVehicleTypes(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<List<vechcleType_Entity>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Retrieve all vehicle types from the repository
            List<vechcleType_Entity> vehicleTypes = vehiclesTypeRepository.findAll();

            ResponseWrapper<List<vechcleType_Entity>> responseWrapper = new ResponseWrapper<>("Vehicle types retrieved successfully", vehicleTypes);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<List<vechcleType_Entity>> responseWrapper = new ResponseWrapper<>("An error occurred while retrieving vehicle types", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }
}
