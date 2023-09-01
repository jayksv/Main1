package com.Auton.gibg.controller.vehicles;
import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.repository.vehicles.*;
import com.Auton.gibg.entity.vehicle.*;
import com.Auton.gibg.response.ResponseWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.management.Query;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/vehicles")
public class vehicles_controller {
    private final JdbcTemplate jdbcTemplate;
    private final authToken authService;
    private final vehicles_repository vehicles_repository;

    @Autowired
    public vehicles_controller(vehicles_repository vehiclesRrepository , authToken authService, JdbcTemplate jdbcTemplate) {
        this.vehicles_repository = vehiclesRrepository;
        this.authService = authService;
        this.jdbcTemplate = jdbcTemplate;

    }


    @PostMapping("/insert")
    public ResponseEntity<ResponseWrapper<String>> insertVehicle(
            @RequestBody vechcle_Entity vehicleEntity,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if required fields are provided
            if (vehicleEntity.getModel() == null || vehicleEntity.getModel().isEmpty() ||
                    vehicleEntity.getYear() == null || vehicleEntity.getColor() == null ||
                    vehicleEntity.getVehicleType() == null) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Required fields are missing.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Save the vehicle using the repository
            vehicles_repository.save(vehicleEntity);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle added successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while adding the vehicle", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @PutMapping("/update/{vehicleId}")
    public ResponseEntity<ResponseWrapper<String>> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody vechcle_Entity vehicleEntity,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if the vehicle with given ID exists
            if (!vehicles_repository.existsById(vehicleId)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Update the vehicle using the repository
            vehicleEntity.setVehicleId(vehicleId); // Set the ID in the entity
            vehicles_repository.save(vehicleEntity);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle updated successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while updating the vehicle", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @GetMapping("/findById/{vehicleId}")
    public ResponseEntity<ResponseWrapper<vechcle_Entity>> getVehicleById(
            @PathVariable Long vehicleId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<vechcle_Entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Find the vehicle by its ID
            Optional<vechcle_Entity> vehicleOptional = vehicles_repository.findById(vehicleId);
            if (!vehicleOptional.isPresent()) {
                ResponseWrapper<vechcle_Entity> responseWrapper = new ResponseWrapper<>("Vehicle not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Vehicle found, return it
            vechcle_Entity vehicle = vehicleOptional.get();
            ResponseWrapper<vechcle_Entity> responseWrapper = new ResponseWrapper<>("Vehicle found", vehicle);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<vechcle_Entity> responseWrapper = new ResponseWrapper<>("An error occurred while fetching the vehicle", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @DeleteMapping("/delete/{vehicleId}")
    public ResponseEntity<ResponseWrapper<String>> deleteVehicleById(
            @PathVariable Long vehicleId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if the vehicle exists
            Optional<vechcle_Entity> vehicleOptional = vehicles_repository.findById(vehicleId);
            if (!vehicleOptional.isPresent()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Delete the vehicle
            vehicles_repository.delete(vehicleOptional.get());

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Vehicle deleted successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while deleting the vehicle", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper<List<vehicleDTO>>> getAllVehicles(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<List<vehicleDTO>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Execute native SQL query to retrieve all vehicles
            String sql = "SELECT tb_vehicles.vehicle_id,tb_vehicles.model, tb_vehicles.year,tb_vehicles.color,tb_vehicle_type.v_name FROM `tb_vehicles` JOIN tb_vehicle_type ON tb_vehicles.vehicle_type= tb_vehicle_type.vtype_id;";
            List<vehicleDTO> vehicles = jdbcTemplate.query(sql, (rs, rowNum) -> {
                vehicleDTO dto = new vehicleDTO();
                dto.setVehicleId(rs.getLong("vehicle_id"));
                dto.setModel(rs.getString("model"));
                dto.setYear(rs.getInt("year"));
                dto.setColor(rs.getString("color"));
                dto.setType(rs.getString("v_name"));

                return dto;
            });

            ResponseWrapper<List<vehicleDTO>> responseWrapper = new ResponseWrapper<>("Vehicles found", vehicles);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<List<vehicleDTO>> responseWrapper = new ResponseWrapper<>("An error occurred while fetching vehicles", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }



}
