package com.Auton.gibg.controller.user;


import com.Auton.gibg.entity.address.address_entity;
import com.Auton.gibg.entity.shop.*;
import com.Auton.gibg.entity.user.LoginResponse;
import com.Auton.gibg.entity.user.UserAddressShopWrapper;
import com.Auton.gibg.entity.user.UserAddressWrapper;
import com.Auton.gibg.entity.user.user_entity;
import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.repository.address.address_repoittory;
import com.Auton.gibg.repository.shop.*;
import com.Auton.gibg.repository.user.user_repository;
import com.Auton.gibg.response.ResponseWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Allow requests from any origin
@Api(tags = "User Management")
public class user_controller {
    private final JdbcTemplate jdbcTemplate;
    private final authToken authService;
    @Value("${jwt_secret}")
    private String jwt_secret;
    @Autowired
    private user_repository user_repository;

    @Autowired
    private address_repoittory address_repoittory;

    @Autowired
    private shop_repostory shop_repostory;

    @Autowired
    private shopAmenrities_repository shopAmenritiesRepository;

    @Autowired
    private shopService_repository shopServiceRepository;

    @Autowired
    private shopImage_repository shopImageRepository;

    @Autowired
    private shopType_repository shopTypeRepository;

    @Autowired
    public user_controller(JdbcTemplate jdbcTemplate, authToken authService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<LoginResponse>> loginUser(@RequestBody user_entity user) {
        try {
            // Validate email format
            if (!isValidEmail(user.getEmail())) {
                ResponseWrapper<LoginResponse> responseWrapper = new ResponseWrapper<>("Invalid email format.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Check for null or invalid password
            if (user.getPassword() == null || user.getPassword().length() < 8) {
                ResponseWrapper<LoginResponse> responseWrapper = new ResponseWrapper<>("Invalid password. Password should be at least 8 characters long.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Custom SQL query to retrieve user by email
            String sql = "SELECT tb_users.user_id, tb_users.email, tb_users.first_name, tb_users.last_name, tb_users.role_id,tb_users.password, tb_role.role_name FROM tb_users JOIN tb_role ON tb_users.role_id = tb_role.role_id " +
                    "WHERE is_active = 'Active' AND (tb_users.role_id = 1 OR tb_users.role_id = 2)  and email = ? ";
            user_entity userExist = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, (resultSet, rowNum) -> {
                user_entity userEntity = new user_entity();
                userEntity.setUser_id(resultSet.getLong("user_id"));
                userEntity.setEmail(resultSet.getString("email"));
                userEntity.setFirst_name(resultSet.getString("first_name"));
                userEntity.setLast_name(resultSet.getString("last_name"));
                userEntity.setGender(resultSet.getString("role_name"));
                userEntity.setPassword(resultSet.getString("password"));
                userEntity.setRole_id(resultSet.getLong("role_id"));
                return userEntity;
            });

            if (userExist != null) {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                if (bcrypt.matches(user.getPassword(), userExist.getPassword())) {
                    // Build custom claims
                    Claims claims = Jwts.claims();
                    claims.setSubject(userExist.getEmail());
                    claims.put("user_id", userExist.getUser_id());
                    claims.put("first_name", userExist.getFirst_name());
                    claims.put("last_name", userExist.getLast_name());
                    claims.put("role_name", userExist.getGender());
                    claims.put("role_id", userExist.getRole_id());

                    String token = Jwts.builder()
                            .setClaims(claims)
                            .setIssuedAt(new Date(System.currentTimeMillis()))
                            .setExpiration(new Date(System.currentTimeMillis() + 86400000 * 24)) // Token will expire in 24 hours
                            .signWith(SignatureAlgorithm.HS512, jwt_secret)
                            .compact();

                    LoginResponse loginResponse = new LoginResponse(token, null);
                    ResponseWrapper<LoginResponse> responseWrapper = new ResponseWrapper<>("Login successful.", loginResponse);
                    return ResponseEntity.ok(responseWrapper);
                }
            }

            // Invalid credentials or user not found
            ResponseWrapper<LoginResponse> responseWrapper = new ResponseWrapper<>("Invalid credentials.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);

        } catch (EmptyResultDataAccessException e) {
            // User not found
            ResponseWrapper<LoginResponse> responseWrapper = new ResponseWrapper<>("User not found.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            String errorMessage = "An error occurred while logging in.";
            // Handle the error and return an error response
            ResponseWrapper<LoginResponse> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


// create new sub admin

    @PostMapping("/user/add_subadmin")
    public ResponseEntity<ResponseWrapper<List<user_entity>>> addNewUser(@RequestBody UserAddressWrapper userAddressWrapper, @RequestHeader("Authorization") String authorizationHeader) {
        try {

            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                // Token is invalid or has expired
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            user_entity user = userAddressWrapper.getUser();
            address_entity userAddress = userAddressWrapper.getUserAddress();


            // Check for null values and validate email and password length
            if (user.getEmail() == null || user.getPassword() == null || user.getFirst_name() == null || user.getLast_name() == null) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Email, password, first name, and last name are required.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            if (!isValidEmail(user.getEmail())) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Invalid email format.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            //check password
            String password = user.getPassword();

            if (password.length() < 8) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should be at least 8 characters long.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[a-z].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one lowercase letter.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[A-Z].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one uppercase letter.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*\\d.*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one digit.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[@#$%^&+=].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one special character.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            // Check if email already exists
            user_entity emailExist = user_repository.findByEmail(user.getEmail());

            if (emailExist != null) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Email is already taken.", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseWrapper);
            } else {
                // Save the user address
                address_entity savedAddress = address_repoittory.save(userAddress);

                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                String encryptedPass = bcrypt.encode(user.getPassword());
                user.setPassword(encryptedPass);

                user.setIs_active("Active");
                user.setAddress_id(savedAddress.getAddressId().longValue());
                user.setRole_id((long) 2);

                user_entity savedUser = user_repository.save(user);

                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Insert new user and address successful", null);
                return ResponseEntity.ok(responseWrapper);
            }


        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while adding a new user.";
            ResponseWrapper<List<user_entity>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // update sub addmin
    @PutMapping("/user/update_subadmin/{userId}")
    public ResponseEntity<ResponseWrapper<user_entity>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserAddressWrapper userAddressWrapper,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                // Token is invalid or has expired
                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }
            // Find the existing user by user_id
            Optional<user_entity> existingUserOptional = user_repository.findById(userId);


            if (existingUserOptional.isEmpty()) {
                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }
            Optional<address_entity> existingAddress = address_repoittory.findById(existingUserOptional.get().getAddress_id());
            user_entity user = userAddressWrapper.getUser();
            address_entity userAddress = userAddressWrapper.getUserAddress();


            // Update user properties
            user_entity userToUpdate = existingUserOptional.get();
//        userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setFirst_name(user.getFirst_name());
            userToUpdate.setLast_name(user.getLast_name());
            userToUpdate.setBirthdate(user.getBirthdate());
            userToUpdate.setGender(user.getGender());
            userToUpdate.setProfile_picture(user.getProfile_picture());
            userToUpdate.setBio(user.getBio());
//        userToUpdate.setRole_id(user.getRole_id());

            // Update address properties
            address_entity addressToUpdate = existingAddress.get();
            addressToUpdate.setStreetAddress(userAddress.getStreetAddress());
            addressToUpdate.setState(userAddress.getState());
            addressToUpdate.setPostalCode(userAddress.getPostalCode());
            addressToUpdate.setCountry(userAddress.getCountry());
            addressToUpdate.setLatitude(userAddress.getLatitude());
            addressToUpdate.setLongitude(userAddress.getLongitude());

            // Check if email already exists
            user_entity emailExist = user_repository.findByEmail(userToUpdate.getEmail());
            if (emailExist != null) {
                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Email is already taken.", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseWrapper);
            }

            // Save the updated user
            user_entity updatedUserEntity = user_repository.save(userToUpdate);
            address_entity updatedAddress = address_repoittory.save(addressToUpdate);

            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User updated successfully.", null);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while updating the user.";
            ResponseWrapper<user_entity> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // delete the user
    @DeleteMapping("/user/delete_subadmin/{userId}")
    public ResponseEntity<ResponseWrapper<String>> deleteUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
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
            Long authenticatedUserId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the authenticated user has the appropriate role to perform this action (e.g., admin)
            if (!"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            Optional<user_entity> existingUserOptional = user_repository.findById(userId);


            if (existingUserOptional.isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }


            user_entity userToDelete = existingUserOptional.get();
            Long addressIdToDelete = userToDelete.getAddress_id();


            // Delete the associated address
            if (addressIdToDelete != null) {
                address_repoittory.deleteById(addressIdToDelete);
            }

            // Delete the user
            user_repository.delete(userToDelete);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User deleted successfully.", null);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (EmptyResultDataAccessException e) {
            // User not found
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while deleting the user.";
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    //----------------------------------------------------------------Manage shop owner information
    @PostMapping("/user/add_shopowner")
    public ResponseEntity<ResponseWrapper<List<user_entity>>> addNewShowOwner(@RequestBody UserAddressShopWrapper request,
                                                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {

            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            user_entity user = request.getUser();
//        address_entity userAddress = request.getUserAddress();
            shop_entity userShop = request.getUserShop();
            List<shopAmenrities_entity> shopAmenrities = request.getShopAmenrities();
            List<shopService_entity> shopService = request.getShopServices();
            List<shopImage_entity> shopImages = request.getShopImages();
            List<shop_type> shopTypes = request.getShopType();

            System.out.println(user.getPhone());

            // Check for null values and validate email and password length
            if (user.getEmail() == null || user.getPassword() == null || user.getFirst_name() == null || user.getLast_name() == null) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Email, password, first name, and last name are required.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            if (!isValidEmail(user.getEmail())) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Invalid email format.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            //check password
            String password = user.getPassword();

            if (password.length() < 8) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should be at least 8 characters long.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[a-z].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one lowercase letter.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[A-Z].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one uppercase letter.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*\\d.*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one digit.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[@#$%^&+=].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one special character.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Check if email already exists
            user_entity emailExist = user_repository.findByEmail(user.getEmail());

            if (emailExist != null) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Email is already taken.", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseWrapper);
            } else {
                // Save the user address
//            address_entity savedAddress = address_repoittory.save(userAddress);
                // shop information
                userShop.setShop_status_id((long) 2);
                shop_entity savedShop = shop_repostory.save(userShop);


                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                String encryptedPass = bcrypt.encode(user.getPassword());
                user.setPassword(encryptedPass);

                user.setIs_active("Active");
//            user.setAddress_id(savedAddress.getAddressId().longValue());
                user.setShop_id(savedShop.getShopId().longValue());
                user.setRole_id((long) 3);


                user_entity savedUser = user_repository.save(user);
                // Set the shop_id

                int minSize = Math.min(shopAmenrities.size(), Math.min(shopService.size(), Math.min(shopImages.size(),shopTypes.size())));

                for (int i = 0; i < minSize; i++) {
                    shopAmenrities_entity amenrity = shopAmenrities.get(i);
                    shopService_entity service = shopService.get(i);
                    shopImage_entity image = shopImages.get(i);
                    shop_type type = shopTypes.get(i);

                    amenrity.setShop_id(savedUser.getShop_id());
                    service.setShop_id(savedUser.getShop_id());
                    image.setShop_id(savedUser.getShop_id());
                    type.setShop_id(savedUser.getShop_id());
                }

                // Set the shop_id for each shopImage_entity
                for (shopImage_entity image : shopImages) {
                    image.setShop_id(savedUser.getShop_id());
                }

                shopAmenritiesRepository.saveAll(shopAmenrities);
                shopServiceRepository.saveAll(shopService);
                shopImageRepository.saveAll(shopImages);
                shopTypeRepository.saveAll(shopTypes);


                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Insert new user, address and shop successful", null);
                return ResponseEntity.ok(responseWrapper);
            }


        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while adding a new user.";
            ResponseWrapper<List<user_entity>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/user/update_shopowner/{userId}")
    public ResponseEntity<ResponseWrapper<user_entity>> updateUserWithShopOwner(
            @PathVariable Long userId,
            @RequestBody UserAddressShopWrapper updateRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            user_entity updatedUser = updateRequest.getUser();
//            address_entity updatedAddress = updateRequest.getUserAddress();
            shop_entity updatedShop = updateRequest.getUserShop();
            List<shopAmenrities_entity> shopAmenrities = updateRequest.getShopAmenrities();
            List<shopService_entity> shopService = updateRequest.getShopServices();
            List<shopImage_entity> shopImages = updateRequest.getShopImages();

            // Find the existing user by ID
            Optional<user_entity> userOptional = user_repository.findById(userId);

            if (userOptional.isEmpty()) {
                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            Optional<address_entity> existingAddress = address_repoittory.findById(userOptional.get().getAddress_id());
            Optional<shop_entity> existingShop = shop_repostory.findById(userOptional.get().getShop_id());

            user_entity existingUser = userOptional.get();
            address_entity addressToUpdate = existingAddress.get();
            shop_entity shopToUpdate = existingShop.get();


            // Update user's information
            user_entity userToUpdate = userOptional.get();
//            userToUpdate.setEmail(updatedUser.getEmail());
            userToUpdate.setPassword(updatedUser.getPassword());
            userToUpdate.setFirst_name(updatedUser.getFirst_name());
            userToUpdate.setLast_name(updatedUser.getLast_name());
            userToUpdate.setBirthdate(updatedUser.getBirthdate());
            userToUpdate.setGender(updatedUser.getGender());
            userToUpdate.setProfile_picture(updatedUser.getProfile_picture());
            userToUpdate.setBio(updatedUser.getBio());
            userToUpdate.setRole_id(updatedUser.getRole_id());
            userToUpdate.setPhone(updatedUser.getPhone());
            userToUpdate.setRole_id((long) 3);

            // Update shop's information
            shop_entity shopOwnToUpdate = existingShop.get();
            shopOwnToUpdate.setShopName(updatedShop.getShopName());
            shopOwnToUpdate.setStreetAddress(updatedShop.getStreetAddress());
            shopOwnToUpdate.setCity(updatedShop.getCity());
            shopOwnToUpdate.setState(updatedShop.getState());
            shopOwnToUpdate.setPostalCode(updatedShop.getPostalCode());
            shopOwnToUpdate.setCountry(updatedShop.getCountry());
            shopOwnToUpdate.setLongitude(updatedShop.getLongitude());
            shopOwnToUpdate.setLatitude(updatedShop.getLatitude());
            shopOwnToUpdate.setShop_type_id(updatedShop.getShop_type_id());
            shopOwnToUpdate.setShop_phone(updatedShop.getShop_phone());
            shopOwnToUpdate.setShop_mail(updatedShop.getShop_mail());
            shopOwnToUpdate.setShop_website(updatedShop.getShop_website());

            shopOwnToUpdate.setMondayOpen(updatedShop.getMondayOpen());
            shopOwnToUpdate.setMondayClose(updatedShop.getMondayClose());
            shopOwnToUpdate.setTuesday_open(updatedShop.getTuesday_open());
            shopOwnToUpdate.setTuesday_close(updatedShop.getTuesday_close());
            shopOwnToUpdate.setWednesday_open(updatedShop.getWednesday_open());
            shopOwnToUpdate.setWednesday_close(updatedShop.getWednesday_close());
            shopOwnToUpdate.setThursday_open(updatedShop.getThursday_open());
            shopOwnToUpdate.setThursday_close(updatedShop.getThursday_close());
            shopOwnToUpdate.setFriday_open(updatedShop.getFriday_open());
            shopOwnToUpdate.setFriday_close(updatedShop.getFriday_close());
            shopOwnToUpdate.setSaturday_open(updatedShop.getSaturday_open());
            shopOwnToUpdate.setSaturday_close(updatedShop.getSaturday_close());
            shopOwnToUpdate.setSunday_open(updatedShop.getSunday_open());
            shopOwnToUpdate.setSunday_close(updatedShop.getSunday_close());
            shopToUpdate.setShop_type_id(updatedShop.getShop_type_id());


//            // Update address information
//            address_entity addressToUpdates = existingAddress.get();
//            addressToUpdates.setStreetAddress(updatedAddress.getStreetAddress());
//            addressToUpdates.setState(updatedAddress.getState());
//            addressToUpdates.setPostalCode(updatedAddress.getPostalCode());
//            addressToUpdates.setCountry(updatedAddress.getCountry());
//            addressToUpdates.setLatitude(updatedAddress.getLatitude());
//            addressToUpdates.setLongitude(updatedAddress.getLongitude());

            // Update shop amenities, services, and images
            updateShopAmenities(existingUser.getShop_id(), shopAmenrities);
            updateShopServices(existingUser.getShop_id(), shopService);
            updateShopImages(existingUser.getShop_id(), shopImages);


            // Save the updated entities
            user_repository.save(existingUser);
            shop_repostory.save(shopToUpdate);
            address_repoittory.save(addressToUpdate);


            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User information updated successfully.", null);
            return ResponseEntity.ok(responseWrapper);


        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while updating the user information.";
            ResponseWrapper<user_entity> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /// for update
    private void updateShopAmenities(Long shopId, List<shopAmenrities_entity> updatedAmenities) {
        String deleteOldAmenitiesQuery = "DELETE FROM shop_amenitie WHERE shop_id = ?";
        jdbcTemplate.update(deleteOldAmenitiesQuery, shopId);

        String insertNewAmenityQuery = "INSERT INTO shop_amenitie (shop_id, amenities_id) VALUES (?, ?)";
        for (shopAmenrities_entity amenity : updatedAmenities) {
            jdbcTemplate.update(insertNewAmenityQuery, shopId, amenity.getAmenities_id());
        }
    }

    private void updateShopServices(Long shopId, List<shopService_entity> updatedServices) {
        String deleteOldServicesQuery = "DELETE FROM shop_service WHERE shop_id = ?";
        jdbcTemplate.update(deleteOldServicesQuery, shopId);

        String insertNewServiceQuery = "INSERT INTO shop_service (shop_id, service_id) VALUES (?, ?)";
        for (shopService_entity service : updatedServices) {
            jdbcTemplate.update(insertNewServiceQuery, shopId, service.getService_id());
        }
    }

    private void updateShopImages(Long shopId, List<shopImage_entity> updatedImages) {
        String deleteOldImagesQuery = "DELETE FROM shop_image WHERE shop_id = ?";
        jdbcTemplate.update(deleteOldImagesQuery, shopId);

        String insertNewImageQuery = "INSERT INTO shop_image (shop_id, image_path) VALUES (?, ?)";
        for (shopImage_entity image : updatedImages) {
            jdbcTemplate.update(insertNewImageQuery, shopId, image.getImage_path());
        }
    }

    // for update
    @DeleteMapping("/user/delete_shopowner/{userId}")
    public ResponseEntity<ResponseWrapper<String>> deleteshopowner(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {

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
            Long authenticatedUserId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the authenticated user has the appropriate role to perform this action (e.g., admin)
            if (!"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            // Find the existing user by ID
            Optional<user_entity> userOptional = user_repository.findById(userId);

            if (userOptional.isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            user_entity existingUser = userOptional.get();
//            Long addressId = existingUser.getAddress_id();
            Long shopId = existingUser.getShop_id();

            // Delete associated address
//            address_repoittory.deleteById(addressId);

            // Delete associated shop owner
            shop_repostory.deleteById(shopId);

            // Delete user
            user_repository.deleteById(userId);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User and associated records deleted successfully.", null);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while deleting the user.";
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //------------------------------ manage general user

    @PostMapping("/user/add_general_user")
    public ResponseEntity<ResponseWrapper<List<user_entity>>> addNewGeneraluser(@RequestBody UserAddressWrapper userAddressWrapper, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                // Token is invalid or has expired
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            user_entity user = userAddressWrapper.getUser();
            address_entity userAddress = userAddressWrapper.getUserAddress();


            // Check for null values and validate email and password length
            if (user.getEmail() == null || user.getPassword() == null || user.getFirst_name() == null || user.getLast_name() == null) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Email, password, first name, and last name are required.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            if (!isValidEmail(user.getEmail())) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Invalid email format.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            //check password
            String password = user.getPassword();

            if (password.length() < 8) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should be at least 8 characters long.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[a-z].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one lowercase letter.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[A-Z].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one uppercase letter.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*\\d.*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one digit.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }
            if (!password.matches(".*[@#$%^&+=].*")) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Password should contain at least one special character.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            // Check if email already exists
            user_entity emailExist = user_repository.findByEmail(user.getEmail());

            if (emailExist != null) {
                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Email is already taken.", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseWrapper);
            } else {
                // Save the user address
                address_entity savedAddress = address_repoittory.save(userAddress);

                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                String encryptedPass = bcrypt.encode(user.getPassword());
                user.setPassword(encryptedPass);

                user.setIs_active("Active");
                user.setAddress_id(savedAddress.getAddressId().longValue());
                user.setRole_id((long) 4);

                user_entity savedUser = user_repository.save(user);

                ResponseWrapper<List<user_entity>> responseWrapper = new ResponseWrapper<>("Insert new user and address successful", null);
                return ResponseEntity.ok(responseWrapper);
            }


        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while adding a new user.";
            ResponseWrapper<List<user_entity>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

//    @PutMapping("/user/update_general_user/{userId}")
//    public ResponseEntity<ResponseWrapper<user_entity>> updateGeneralUser(
//            @PathVariable Long userId,
//            @RequestBody UserAddressShopWrapper userAddressShopWrapper,
//            @RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            if (authorizationHeader == null || authorizationHeader.isBlank()) {
//                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
//            }
//
//            // Verify the token from the Authorization header
//            String token = authorizationHeader.substring("Bearer ".length());
//
//            Claims claims = Jwts.parser()
//                    .setSigningKey(jwt_secret) // Replace with your secret key
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            // Check token expiration
//            Date expiration = claims.getExpiration();
//            if (expiration != null && expiration.before(new Date())) {
//                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
//            }
//
//            // Extract necessary claims (you can add more as needed)
//            Long authenticatedUserId = claims.get("user_id", Long.class);
//            String role = claims.get("role_name", String.class);
//
//            // Check if the authenticated user has the appropriate role to perform this action (e.g., admin)
//            if (!"admin".equalsIgnoreCase(role) && !"Super Admin".equalsIgnoreCase(role)) {
//                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
//            }
//
//            user_entity updatedUser = userAddressShopWrapper.getUser();
//            address_entity updatedAddress = userAddressShopWrapper.getUserAddress();
//            shop_entity updatedShop = userAddressShopWrapper.getUserShop();
//
//            // Find the existing user by ID
//            Optional<user_entity> userOptional = user_repository.findById(userId);
//
//            if (userOptional.isEmpty()) {
//                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User not found.", null);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
//            }
//
//            Optional<address_entity> existingAddress = address_repoittory.findById(userOptional.get().getAddress_id());
//            Optional<shop_entity> existingShop = shop_repostory.findById(userOptional.get().getShop_id());
//
//            user_entity existingUser = userOptional.get();
//            address_entity addressToUpdate = existingAddress.get();
//            shop_entity shopToUpdate = existingShop.get();
//
//
//            // Update user's information
//            user_entity userToUpdate = userOptional.get();
//            userToUpdate.setEmail(updatedUser.getEmail());
//            userToUpdate.setPassword(updatedUser.getPassword());
//            userToUpdate.setFirst_name(updatedUser.getFirst_name());
//            userToUpdate.setLast_name(updatedUser.getLast_name());
//            userToUpdate.setBirthdate(updatedUser.getBirthdate());
//            userToUpdate.setGender(updatedUser.getGender());
//            userToUpdate.setProfile_picture(updatedUser.getProfile_picture());
//            userToUpdate.setBio(updatedUser.getBio());
//            userToUpdate.setRole_id(updatedUser.getRole_id());
//
//            // Update shop's information
//            shop_entity shopOwnToUpdate = existingShop.get();
//            shopOwnToUpdate.setShopName(updatedShop.getShopName());
//            shopOwnToUpdate.setStreetAddress(updatedShop.getStreetAddress());
//            shopOwnToUpdate.setCity(updatedShop.getCity());
//            shopOwnToUpdate.setState(updatedShop.getState());
//            shopOwnToUpdate.setPostalCode(updatedShop.getPostalCode());
//            shopOwnToUpdate.setCountry(updatedShop.getCountry());
//            shopOwnToUpdate.setLongitude(updatedShop.getLongitude());
//            shopOwnToUpdate.setLatitude(updatedShop.getLatitude());
//            shopOwnToUpdate.setMondayOpen(updatedShop.getMondayOpen());
//            shopOwnToUpdate.setMondayClose(updatedShop.getMondayClose());
//            shopOwnToUpdate.setTuesday_open(updatedShop.getTuesday_open());
//            shopOwnToUpdate.setTuesday_close(updatedShop.getTuesday_close());
//            shopOwnToUpdate.setWednesday_open(updatedShop.getWednesday_open());
//            shopOwnToUpdate.setWednesday_close(updatedShop.getWednesday_close());
//            shopOwnToUpdate.setThursday_open(updatedShop.getThursday_open());
//            shopOwnToUpdate.setThursday_close(updatedShop.getThursday_close());
//            shopOwnToUpdate.setFriday_open(updatedShop.getFriday_open());
//            shopOwnToUpdate.setFriday_close(updatedShop.getFriday_close());
//            shopOwnToUpdate.setSaturday_open(updatedShop.getSaturday_open());
//            shopOwnToUpdate.setSaturday_close(updatedShop.getSaturday_close());
//            shopOwnToUpdate.setSunday_open(updatedShop.getSunday_open());
//            shopOwnToUpdate.setSunday_close(updatedShop.getSunday_close());
//
//
//            // Update address information
//            address_entity addressToUpdates = existingAddress.get();
//            addressToUpdates.setStreetAddress(updatedAddress.getStreetAddress());
//            addressToUpdates.setState(updatedAddress.getState());
//            addressToUpdates.setPostalCode(updatedAddress.getPostalCode());
//            addressToUpdates.setCountry(updatedAddress.getCountry());
//            addressToUpdates.setLatitude(updatedAddress.getLatitude());
//            addressToUpdates.setLongitude(updatedAddress.getLongitude());
//
//            // Save the updated entities
//            user_repository.save(existingUser);
//            shop_repostory.save(shopToUpdate);
//            address_repoittory.save(addressToUpdate);
//
//
//            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User information updated successfully.", null);
//            return ResponseEntity.ok(responseWrapper);
//
//        } catch (JwtException e) {
//            // Token is invalid or has expired
//            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
//        } catch (Exception e) {
//            e.printStackTrace();
//            String errorMessage = "An error occurred while updating the user information.";
//            ResponseWrapper<user_entity> errorResponse = new ResponseWrapper<>(errorMessage, null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }


    @PutMapping("/user/update_general_user/{userId}")
    public ResponseEntity<ResponseWrapper<user_entity>> updateGeneralUser(
            @PathVariable Long userId,
            @RequestBody UserAddressWrapper userAddressWrapper,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                // Token is invalid or has expired
                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }
            // Find the existing user by user_id
            Optional<user_entity> existingUserOptional = user_repository.findById(userId);


            if (existingUserOptional.isEmpty()) {
                ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }
            Optional<address_entity> existingAddress = address_repoittory.findById(existingUserOptional.get().getAddress_id());
            user_entity user = userAddressWrapper.getUser();
            address_entity userAddress = userAddressWrapper.getUserAddress();


            // Update user properties
            user_entity userToUpdate = existingUserOptional.get();
//            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setFirst_name(user.getFirst_name());
            userToUpdate.setLast_name(user.getLast_name());
            userToUpdate.setBirthdate(user.getBirthdate());
            userToUpdate.setGender(user.getGender());
            userToUpdate.setProfile_picture(user.getProfile_picture());
            userToUpdate.setBio(user.getBio());
//            userToUpdate.setRole_id(user.getRole_id());

            // Update address properties
            address_entity addressToUpdate = existingAddress.get();
            addressToUpdate.setStreetAddress(userAddress.getStreetAddress());
            addressToUpdate.setState(userAddress.getState());
            addressToUpdate.setPostalCode(userAddress.getPostalCode());
            addressToUpdate.setCountry(userAddress.getCountry());
            addressToUpdate.setLatitude(userAddress.getLatitude());
            addressToUpdate.setLongitude(userAddress.getLongitude());


            // Save the updated user
            user_entity updatedUserEntity = user_repository.save(userToUpdate);
            address_entity updatedAddress = address_repoittory.save(addressToUpdate);

            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("User updated successfully.", null);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<user_entity> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while updating the user.";
            ResponseWrapper<user_entity> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/user/delete_general_user/{userId}")
    public ResponseEntity<ResponseWrapper<String>> deleteGeneraluser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
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
            Long authenticatedUserId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the authenticated user has the appropriate role to perform this action (e.g., admin)
            if (!"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            Optional<user_entity> existingUserOptional = user_repository.findById(userId);


            if (existingUserOptional.isEmpty()) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }


            user_entity userToDelete = existingUserOptional.get();
            Long addressIdToDelete = userToDelete.getAddress_id();


            // Delete the associated address
            if (addressIdToDelete != null) {
                address_repoittory.deleteById(addressIdToDelete);
            }

            // Delete the user
            user_repository.delete(userToDelete);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User deleted successfully.", null);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (EmptyResultDataAccessException e) {
            // User not found
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An error occurred while deleting the user.";
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Method to validate email format
    private boolean isValidEmail(String email) {
        // Regular expression for a simple email format check
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }


}
