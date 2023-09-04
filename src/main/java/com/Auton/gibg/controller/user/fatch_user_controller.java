package com.Auton.gibg.controller.user;

import com.Auton.gibg.entity.product.color_entity;
import com.Auton.gibg.entity.product.product_image_entity;
import com.Auton.gibg.entity.product.size_entity;
import com.Auton.gibg.entity.shop.*;
import com.Auton.gibg.repository.product.color_repository;
import com.Auton.gibg.repository.product.product_image_repository;
import com.Auton.gibg.repository.product.product_repository;
import com.Auton.gibg.repository.product.size_repository;
import com.Auton.gibg.repository.shop.shopAmenrities_repository;
import com.Auton.gibg.repository.shop.shopImage_repository;
import com.Auton.gibg.repository.shop.shopService_repository;
import com.Auton.gibg.repository.shop.shopType_repository;
import com.Auton.gibg.response.usersDTO.*;
import com.Auton.gibg.entity.user.user_entity;
import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.response.product.productColorSizeImageDTO;
import com.Auton.gibg.response.product.productDTO;
import com.Auton.gibg.response.shopDTO.shopAllDTO;
import com.Auton.gibg.response.usersDTO.usersAllDTO;
import com.Auton.gibg.response.usersDTO.userById_subadminDTO;
import com.Auton.gibg.response.usersDTO.userById_shopOwner;
import com.Auton.gibg.response.ResponseWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Allow requests from any origin
@Api(tags = "User Management")
public class fatch_user_controller {
    @Value("${jwt_secret}")
    private String jwt_secret;
    private final JdbcTemplate jdbcTemplate;
    private final authToken authService;
    @Autowired
    private shopAmenrities_repository AmenityRepository;
    @Autowired
    private shopImage_repository ImageRepository;

    @Autowired
    private shopService_repository ServiceRepository;
    @Autowired
    private shopType_repository TypeRepository;
    @Autowired
    public fatch_user_controller(JdbcTemplate jdbcTemplate,authToken authService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authService = authService;
    }

    @GetMapping("/user/all")
    public ResponseEntity<ResponseWrapper<List<usersAllDTO>>> getAllUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<List<usersAllDTO>> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
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
                ResponseWrapper<List<usersAllDTO>> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long userId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role) && !"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<List<usersAllDTO>> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            String sql = "SELECT u.`user_id`, u.`email`, u.`first_name`, u.`last_name`, u.`birthdate`, u.`gender`, u.`profile_picture`, u.`created_at`, u.`last_login`, u.`is_active`, u.`bio`, u.`role_id`, u.`address_id`, u.`shop_id`, a.`street_address`, r.`role_name`, s.`shop_name` FROM `tb_users` u LEFT JOIN `tb_address` a ON u.`address_id` = a.`address_id` LEFT JOIN `tb_role` r ON u.`role_id` = r.`role_id` LEFT JOIN `tb_shop` s ON u.`shop_id` = s.`shop_id`  WHERE u.`role_id` <> 1 ORDER BY u.`user_id` DESC ";
            List<usersAllDTO> users = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
                usersAllDTO usersDTO = new usersAllDTO();
                usersDTO.setUser_id(resultSet.getLong("user_id"));
                usersDTO.setEmail(resultSet.getString("email"));
                usersDTO.setFirst_name(resultSet.getString("first_name"));
                usersDTO.setLast_name(resultSet.getString("last_name"));
                usersDTO.setBirthdate(resultSet.getDate("birthdate"));
                usersDTO.setGender(resultSet.getString("gender"));
                usersDTO.setProfile_picture(resultSet.getString("profile_picture"));
                usersDTO.setCreated_at(resultSet.getDate("created_at"));
                usersDTO.setLast_login(resultSet.getDate("last_login"));
                usersDTO.setIs_active(resultSet.getString("is_active"));
                usersDTO.setBio(resultSet.getString("bio"));
                usersDTO.setStreet_address(resultSet.getString("street_address"));
                usersDTO.setRole_name(resultSet.getString("role_name"));
                usersDTO.setShop_name(resultSet.getString("shop_name"));
                return usersDTO;
            });

            ResponseWrapper<List<usersAllDTO>> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", users);
            return ResponseEntity.ok(responseWrapper);
        }
        catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<List<usersAllDTO>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        }

        catch (Exception e) {
            String errorMessage = "An error occurred while retrieving user data.";
            ResponseWrapper<List<usersAllDTO>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ---------------------------------------------------------------- sub admin

    @GetMapping("/user/sub/findById/{userId}")
    public ResponseEntity<ResponseWrapper<userById_subadminDTO>> getUserById(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }
            // Check if the user exists with the given userId
            String checkUserSql = "SELECT COUNT(*) FROM `tb_users` WHERE `user_id` = ? and tb_users.role_id = 2";
            int userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);

            if (userCount == 0) {
                ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }


            // Query the database to retrieve the user data by userId
            String sql = "SELECT tb_users.`user_id`, tb_users.`email`, tb_users.`first_name`, tb_users.`last_name`, tb_users.`birthdate`, tb_users.`gender`, tb_users.`profile_picture`, tb_users.`created_at`, tb_users.`last_login`, tb_users.`is_active`, tb_users.`bio`, tb_role.role_name, tb_address.street_address, tb_address.state, tb_address.postal_code, tb_address.country, tb_address.latitude, tb_address.longitude FROM `tb_users` JOIN tb_role ON tb_users.role_id = tb_role.role_id JOIN tb_address ON tb_users.address_id = tb_address.address_id WHERE tb_users.user_id = ? and tb_users.role_id = 2";
            userById_subadminDTO userDTO = jdbcTemplate.queryForObject(sql, new Object[]{userId}, (resultSet, rowNum) -> {
                userById_subadminDTO userSubadminDTO = new userById_subadminDTO();

                userSubadminDTO.setUser_id(resultSet.getLong("user_id"));
                userSubadminDTO.setEmail(resultSet.getString("email"));
                userSubadminDTO.setFirst_name(resultSet.getString("first_name"));
                userSubadminDTO.setLast_name(resultSet.getString("last_name"));
                userSubadminDTO.setBirthdate(resultSet.getDate("birthdate"));
                userSubadminDTO.setGender(resultSet.getString("gender"));
                userSubadminDTO.setProfile_picture(resultSet.getString("profile_picture"));
                userSubadminDTO.setCreated_at(resultSet.getDate("created_at"));
                userSubadminDTO.setLast_login(resultSet.getDate("last_login"));
                userSubadminDTO.setIs_active(resultSet.getString("is_active"));
                userSubadminDTO.setBio(resultSet.getString("bio"));
                userSubadminDTO.setRole_name(resultSet.getString("role_name"));

                // Address information
                userSubadminDTO.setStreetAddress(resultSet.getString("street_address"));
                userSubadminDTO.setState(resultSet.getString("state"));
                userSubadminDTO.setPostalCode(resultSet.getString("postal_code"));
                userSubadminDTO.setCountry(resultSet.getString("country"));
                userSubadminDTO.setLatitude(resultSet.getBigDecimal("latitude"));
                userSubadminDTO.setLongitude(resultSet.getBigDecimal("longitude"));

                return userSubadminDTO;
            });


            ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", userDTO);
            return ResponseEntity.ok(responseWrapper);
        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        }


        catch (Exception e) {
            String errorMessage = "An error occurred while retrieving user data.";
            ResponseWrapper<userById_subadminDTO> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user/sub/all")
    public ResponseEntity<ResponseWrapper<List<userById_subadminDTO>>> getSubAdminOnly(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
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
                ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long authenticatedUserId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role) && !"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            // Query the database to retrieve the user data with specific criteria
            String sql = "SELECT tb_users.`user_id`, tb_users.`email`, tb_users.`first_name`, tb_users.`last_name`, tb_users.`birthdate`, tb_users.`gender`, tb_users.`profile_picture`, tb_users.`created_at`, tb_users.`last_login`, tb_users.`is_active`, tb_users.`bio`, tb_role.role_name, tb_address.street_address, tb_address.state, tb_address.postal_code, tb_address.country, tb_address.latitude, tb_address.longitude FROM `tb_users` JOIN tb_role ON tb_users.role_id = tb_role.role_id JOIN tb_address ON tb_users.address_id = tb_address.address_id WHERE tb_users.role_id = 2";
            List<userById_subadminDTO> userDTOList = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
                userById_subadminDTO userSubadminDTO = new userById_subadminDTO();

                userSubadminDTO.setUser_id(resultSet.getLong("user_id"));
                userSubadminDTO.setEmail(resultSet.getString("email"));
                userSubadminDTO.setFirst_name(resultSet.getString("first_name"));
                userSubadminDTO.setLast_name(resultSet.getString("last_name"));
                userSubadminDTO.setBirthdate(resultSet.getDate("birthdate"));
                userSubadminDTO.setGender(resultSet.getString("gender"));
                userSubadminDTO.setProfile_picture(resultSet.getString("profile_picture"));
                userSubadminDTO.setCreated_at(resultSet.getDate("created_at"));
                userSubadminDTO.setLast_login(resultSet.getDate("last_login"));
                userSubadminDTO.setIs_active(resultSet.getString("is_active"));
                userSubadminDTO.setBio(resultSet.getString("bio"));
                userSubadminDTO.setRole_name(resultSet.getString("role_name"));

                // Address information
                userSubadminDTO.setStreetAddress(resultSet.getString("street_address"));
                userSubadminDTO.setState(resultSet.getString("state"));
                userSubadminDTO.setPostalCode(resultSet.getString("postal_code"));
                userSubadminDTO.setCountry(resultSet.getString("country"));
                userSubadminDTO.setLatitude(resultSet.getBigDecimal("latitude"));
                userSubadminDTO.setLongitude(resultSet.getBigDecimal("longitude"));

                return userSubadminDTO;
            });

            ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", userDTOList);
            return ResponseEntity.ok(responseWrapper);
        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        }


        catch (Exception e) {
            String errorMessage = "An error occurred while retrieving user data.";
            ResponseWrapper<List<userById_subadminDTO>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
//----------------------------------------------------------------general user

    @GetMapping("/user/generalUser/all")
    public ResponseEntity<ResponseWrapper<List<userById_subadminDTO>>> getGeneralUserOnly(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
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
                ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long authenticatedUserId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role) && !"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }

            // Query the database to retrieve the user data with specific criteria
            String sql = "SELECT tb_users.`user_id`, tb_users.`email`, tb_users.`first_name`, tb_users.`last_name`, tb_users.`birthdate`, tb_users.`gender`, tb_users.`profile_picture`, tb_users.`created_at`, tb_users.`last_login`, tb_users.`is_active`, tb_users.`bio`, tb_role.role_name, tb_address.street_address, tb_address.state, tb_address.postal_code, tb_address.country, tb_address.latitude, tb_address.longitude FROM `tb_users` JOIN tb_role ON tb_users.role_id = tb_role.role_id JOIN tb_address ON tb_users.address_id = tb_address.address_id WHERE tb_users.role_id = 4";
            List<userById_subadminDTO> userDTOList = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
                userById_subadminDTO userSubadminDTO = new userById_subadminDTO();

                userSubadminDTO.setUser_id(resultSet.getLong("user_id"));
                userSubadminDTO.setEmail(resultSet.getString("email"));
                userSubadminDTO.setFirst_name(resultSet.getString("first_name"));
                userSubadminDTO.setLast_name(resultSet.getString("last_name"));
                userSubadminDTO.setBirthdate(resultSet.getDate("birthdate"));
                userSubadminDTO.setGender(resultSet.getString("gender"));
                userSubadminDTO.setProfile_picture(resultSet.getString("profile_picture"));
                userSubadminDTO.setCreated_at(resultSet.getDate("created_at"));
                userSubadminDTO.setLast_login(resultSet.getDate("last_login"));
                userSubadminDTO.setIs_active(resultSet.getString("is_active"));
                userSubadminDTO.setBio(resultSet.getString("bio"));
                userSubadminDTO.setRole_name(resultSet.getString("role_name"));

                // Address information
                userSubadminDTO.setStreetAddress(resultSet.getString("street_address"));
                userSubadminDTO.setState(resultSet.getString("state"));
                userSubadminDTO.setPostalCode(resultSet.getString("postal_code"));
                userSubadminDTO.setCountry(resultSet.getString("country"));
                userSubadminDTO.setLatitude(resultSet.getBigDecimal("latitude"));
                userSubadminDTO.setLongitude(resultSet.getBigDecimal("longitude"));

                return userSubadminDTO;
            });

            ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", userDTOList);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<List<userById_subadminDTO>> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        }

        catch (Exception e) {
            String errorMessage = "An error occurred while retrieving user data.";
            ResponseWrapper<List<userById_subadminDTO>> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user/generalUser/findById/{userId}")
    public ResponseEntity<ResponseWrapper<userById_subadminDTO>> getGeneralUserById(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("Authorization header is missing or empty.", null);
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
                ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("Token has expired.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Extract necessary claims (you can add more as needed)
            Long authenticatedUserId = claims.get("user_id", Long.class);
            String role = claims.get("role_name", String.class);

            // Check if the user has the appropriate role to perform this action (e.g., admin)
            if (!"admin".equalsIgnoreCase(role) && !"Super Admin".equalsIgnoreCase(role)) {
                ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
            }
            // Check if the user exists with the given userId
            String checkUserSql = "SELECT COUNT(*) FROM `tb_users` WHERE `user_id` = ? and tb_users.role_id = 4";
            int userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);

            if (userCount == 0) {
                ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            // Query the database to retrieve the user data by userId
            String sql = "SELECT tb_users.`user_id`, tb_users.`email`, tb_users.`first_name`, tb_users.`last_name`, tb_users.`birthdate`, tb_users.`gender`, tb_users.`profile_picture`, tb_users.`created_at`, tb_users.`last_login`, tb_users.`is_active`, tb_users.`bio`, tb_role.role_name, tb_address.street_address, tb_address.state, tb_address.postal_code, tb_address.country, tb_address.latitude, tb_address.longitude FROM `tb_users` JOIN tb_role ON tb_users.role_id = tb_role.role_id JOIN tb_address ON tb_users.address_id = tb_address.address_id WHERE tb_users.role_id = 4 and  tb_users.user_id = ?";
            userById_subadminDTO userDTO = jdbcTemplate.queryForObject(sql, new Object[]{userId}, (resultSet, rowNum) -> {
                userById_subadminDTO userSubadminDTO = new userById_subadminDTO();

                userSubadminDTO.setUser_id(resultSet.getLong("user_id"));
                userSubadminDTO.setEmail(resultSet.getString("email"));
                userSubadminDTO.setFirst_name(resultSet.getString("first_name"));
                userSubadminDTO.setLast_name(resultSet.getString("last_name"));
                userSubadminDTO.setBirthdate(resultSet.getDate("birthdate"));
                userSubadminDTO.setGender(resultSet.getString("gender"));
                userSubadminDTO.setProfile_picture(resultSet.getString("profile_picture"));
                userSubadminDTO.setCreated_at(resultSet.getDate("created_at"));
                userSubadminDTO.setLast_login(resultSet.getDate("last_login"));
                userSubadminDTO.setIs_active(resultSet.getString("is_active"));
                userSubadminDTO.setBio(resultSet.getString("bio"));
                userSubadminDTO.setRole_name(resultSet.getString("role_name"));

                // Address information
                userSubadminDTO.setStreetAddress(resultSet.getString("street_address"));
                userSubadminDTO.setState(resultSet.getString("state"));
                userSubadminDTO.setPostalCode(resultSet.getString("postal_code"));
                userSubadminDTO.setCountry(resultSet.getString("country"));
                userSubadminDTO.setLatitude(resultSet.getBigDecimal("latitude"));
                userSubadminDTO.setLongitude(resultSet.getBigDecimal("longitude"));

                return userSubadminDTO;
            });


            ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", userDTO);
            return ResponseEntity.ok(responseWrapper);
        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);


    } catch (Exception e) {
            String errorMessage = "An error occurred while retrieving user data.";
            ResponseWrapper<userById_subadminDTO> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // for shop owner
//    private List<size_entity> findSizesByProductId(Long productId) {
//        String sql = "SELECT * FROM product_size WHERE product_id = ?";
//        return jdbcTemplate.query(sql, this::mapSizeRow, productId);
//    }

    //---------------------------------------------------------------- shop owner
    @GetMapping("/user/shopOwner/all")
    public ResponseEntity<?> getShopOwnerOnly(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }


            String sql = "SELECT `user_id`, `email`, `first_name`, `last_name`, `birthdate`, `gender`, `profile_picture`, `created_at`, `last_login`, `is_active`, `bio`, `role_id`, `role_name`, `user_address`, `user_state`, `user_postal_code`, `user_country`, `user_latitude`, `user_longitude`, `shop_id`, `shop_name`, `shop_address`, `city`, `shop_state`, `shop_postal_code`, `shop_country`, `shop_latitude`, `shop_longitude`, `type_id`, `shop_image`, `monday_open`, `monday_close`, `tuesday_open`, `tuesday_close`, `wednesday_open`, `wednesday_close`, `thursday_open`, `thursday_close`, `friday_open`, `friday_close`, `saturday_open`, `saturday_close`, `sunday_open`, `sunday_close`, `shop_created_at` FROM `user_shop_view` ";
            List<shopInfo_DTO> shopInfo = jdbcTemplate.query(sql, this::mapShopInfo);

            List<shopOwner_DTO> responses = new ArrayList<>();

            for (shopInfo_DTO usershopInfo : shopInfo) {

                List<shopAmenrities_entity> amenity = findshopAmenrities(usershopInfo.getShop_id());
                List<shopImage_entity> image = findImage(usershopInfo.getShop_id());
                List<shopService_entity> service = findService(usershopInfo.getShop_id());
                List<shop_type> types = findShopType(usershopInfo.getShop_id());


                shopOwner_DTO res =new shopOwner_DTO("Success", usershopInfo, amenity, image, service, types);

                responses.add(res);
            }

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/shopOwner/findById/{userId}")
    public ResponseEntity<ResponseWrapper<userById_shopOwner>> getShopOwnerById(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

                // Query the database to retrieve the specific user data by userId
                String sql = "SELECT * FROM `user_shop_view` WHERE `user_id`=?";
                userById_shopOwner userDTO = jdbcTemplate.queryForObject(sql, new Object[]{userId}, (resultSet, rowNum) -> {
                    userById_shopOwner userOneShopower = new userById_shopOwner();
                    userOneShopower.setUser_id(resultSet.getLong("user_id"));
                    userOneShopower.setEmail(resultSet.getString("email"));
                    userOneShopower.setFirst_name(resultSet.getString("first_name"));
                    userOneShopower.setLast_name(resultSet.getString("last_name"));
                    userOneShopower.setBirthdate(resultSet.getDate("birthdate"));
                    userOneShopower.setGender(resultSet.getString("gender"));
                    userOneShopower.setProfile_picture(resultSet.getString("profile_picture"));
                    userOneShopower.setCreated_at(resultSet.getDate("created_at"));
                    userOneShopower.setLast_login(resultSet.getDate("last_login"));
                    userOneShopower.setIs_active(resultSet.getString("is_active"));
                    userOneShopower.setBio(resultSet.getString("bio"));
                    userOneShopower.setRole_name(resultSet.getString("role_name"));

                    // Populate the address information
                    userOneShopower.setStreetAddress(resultSet.getString("user_address"));
                    userOneShopower.setState(resultSet.getString("user_state"));
                    userOneShopower.setPostalCode(resultSet.getString("user_postal_code"));
                    userOneShopower.setCountry(resultSet.getString("user_country"));
                    userOneShopower.setLatitude(resultSet.getDouble("user_latitude"));
                    userOneShopower.setLongitude(resultSet.getDouble("user_longitude"));

                    // Populate the shop information
                    userOneShopower.setShop_name(resultSet.getString("shop_name"));
                    userOneShopower.setStreet_address(resultSet.getString("shop_address"));
                    userOneShopower.setCity(resultSet.getString("city"));
                    userOneShopower.setShop_state(resultSet.getString("shop_state"));
                    userOneShopower.setPostal_code(resultSet.getString("shop_postal_code"));
                    userOneShopower.setShop_country(resultSet.getString("shop_country"));
                    userOneShopower.setShop_latitude(resultSet.getBigDecimal("shop_latitude"));
                    userOneShopower.setShop_longitude(resultSet.getBigDecimal("shop_longitude"));
                    userOneShopower.setShop_type_name(resultSet.getString("type_id"));
                    userOneShopower.setShop_image(resultSet.getString("shop_image"));
                    userOneShopower.setMonday_open(resultSet.getTime("monday_open"));
                    userOneShopower.setMonday_close(resultSet.getTime("monday_close"));
                    userOneShopower.setTuesday_open(resultSet.getTime("tuesday_open"));
                    userOneShopower.setTuesday_close(resultSet.getTime("tuesday_close"));
                    userOneShopower.setWednesday_open(resultSet.getTime("wednesday_open"));
                    userOneShopower.setWednesday_close(resultSet.getTime("wednesday_close"));
                    userOneShopower.setThursday_open(resultSet.getTime("thursday_open"));
                    userOneShopower.setThursday_close(resultSet.getTime("thursday_close"));
                    userOneShopower.setFriday_open(resultSet.getTime("friday_open"));
                    userOneShopower.setFriday_close(resultSet.getTime("friday_close"));
                    userOneShopower.setSaturday_open(resultSet.getTime("saturday_open"));
                    userOneShopower.setSaturday_close(resultSet.getTime("saturday_close"));
                    userOneShopower.setSunday_open(resultSet.getTime("sunday_open"));
                    userOneShopower.setSunday_close(resultSet.getTime("sunday_close"));
                return userOneShopower;
            });

            if (userDTO == null) {
                // If userDTO is null, the user with the given ID was not found
                ResponseWrapper<userById_shopOwner> responseWrapper = new ResponseWrapper<>("User not found.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
            }

            ResponseWrapper<userById_shopOwner> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", userDTO);
            return ResponseEntity.ok(responseWrapper);

        } catch (JwtException e) {
            // Token is invalid or has expired
            ResponseWrapper<userById_shopOwner> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
        } catch (EmptyResultDataAccessException e) {
            // User not found
            ResponseWrapper<userById_shopOwner> responseWrapper = new ResponseWrapper<>("User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
        } catch (Exception e) {
            String errorMessage = "An error occurred while retrieving user data.";
            ResponseWrapper<userById_shopOwner> errorResponse = new ResponseWrapper<>(errorMessage, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

// -- profiles
@GetMapping("/user/sub/profiles")
public ResponseEntity<ResponseWrapper<userById_subadminDTO>> getProfilesuser(

        @RequestHeader("Authorization") String authorizationHeader
) {
    try {
        // Validate authorization using authService
        ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            ResponseWrapper<Void> authResponseBody = authResponse.getBody();
            return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
        }

        // Verify the token from the Authorization header
        String token = authorizationHeader.substring("Bearer ".length());

        Claims claims = Jwts.parser()
                .setSigningKey(jwt_secret) // Replace with your secret key
                .parseClaimsJws(token)
                .getBody();


        // Extract necessary claims (you can add more as needed)
        Long authenticatedUserId = claims.get("user_id", Long.class);
        String role = claims.get("role_name", String.class);

        // Check if the user has the appropriate role to perform this action (e.g., admin)
        if (!"admin".equalsIgnoreCase(role) && !"Super Admin".equalsIgnoreCase(role)) {
            ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("You are not authorized to perform this action.", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper);
        }
        // Check if the user exists with the given userId
        String checkUserSql = "SELECT COUNT(*) FROM `tb_users` WHERE `user_id` = ? ";
        int userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, authenticatedUserId);

        if (userCount == 0) {
            ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
        }


        // Query the database to retrieve the user data by userId
        String sql = "SELECT tb_users.`user_id`, tb_users.`email`, tb_users.`first_name`, tb_users.`last_name`, tb_users.`birthdate`, tb_users.`gender`, tb_users.`profile_picture`, tb_users.`created_at`, tb_users.`last_login`, tb_users.`is_active`, tb_users.`bio`, tb_role.role_name, tb_address.street_address, tb_address.state, tb_address.postal_code, tb_address.country, tb_address.latitude, tb_address.longitude FROM `tb_users` JOIN tb_role ON tb_users.role_id = tb_role.role_id JOIN tb_address ON tb_users.address_id = tb_address.address_id WHERE tb_users.user_id = ? ";
        userById_subadminDTO userDTO = jdbcTemplate.queryForObject(sql, new Object[]{authenticatedUserId}, (resultSet, rowNum) -> {
            userById_subadminDTO userSubadminDTO = new userById_subadminDTO();

            userSubadminDTO.setUser_id(resultSet.getLong("user_id"));
            userSubadminDTO.setEmail(resultSet.getString("email"));
            userSubadminDTO.setFirst_name(resultSet.getString("first_name"));
            userSubadminDTO.setLast_name(resultSet.getString("last_name"));
            userSubadminDTO.setBirthdate(resultSet.getDate("birthdate"));
            userSubadminDTO.setGender(resultSet.getString("gender"));
            userSubadminDTO.setProfile_picture(resultSet.getString("profile_picture"));
            userSubadminDTO.setCreated_at(resultSet.getDate("created_at"));
            userSubadminDTO.setLast_login(resultSet.getDate("last_login"));
            userSubadminDTO.setIs_active(resultSet.getString("is_active"));
            userSubadminDTO.setBio(resultSet.getString("bio"));
            userSubadminDTO.setRole_name(resultSet.getString("role_name"));

            // Address information
            userSubadminDTO.setStreetAddress(resultSet.getString("street_address"));
            userSubadminDTO.setState(resultSet.getString("state"));
            userSubadminDTO.setPostalCode(resultSet.getString("postal_code"));
            userSubadminDTO.setCountry(resultSet.getString("country"));
            userSubadminDTO.setLatitude(resultSet.getBigDecimal("latitude"));
            userSubadminDTO.setLongitude(resultSet.getBigDecimal("longitude"));

            return userSubadminDTO;
        });


        ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("User data retrieved successfully.", userDTO);
        return ResponseEntity.ok(responseWrapper);
    } catch (JwtException e) {
        // Token is invalid or has expired
        ResponseWrapper<userById_subadminDTO> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
    }


    catch (Exception e) {
        String errorMessage = "An error occurred while retrieving user data.";
        ResponseWrapper<userById_subadminDTO> errorResponse = new ResponseWrapper<>(errorMessage, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}



// shop owner function

    private shopInfo_DTO mapShopInfo(ResultSet rs, int rowNum) throws SQLException {
        shopInfo_DTO shopInfo = new shopInfo_DTO();
        shopInfo.setShop_id(rs.getLong("shop_id"));
        shopInfo.setEmail(rs.getString("email"));
        shopInfo.setShop_name(rs.getString("shop_name"));
        shopInfo.setLast_name(rs.getString("last_name"));
        shopInfo.setStreet_address(rs.getString("street_address"));
        shopInfo.setCity(rs.getString("city"));
        shopInfo.setShop_state(rs.getString("shop_state"));
        shopInfo.setPostal_code(rs.getString("postal_code"));
        shopInfo.setShop_country(rs.getString("shop_country"));
        shopInfo.setShop_latitude(rs.getBigDecimal("shop_latitude"));
        shopInfo.setShop_longitude(rs.getBigDecimal("shop_longitude"));
        shopInfo.setShop_type_name(rs.getString("shop_type_name"));
        shopInfo.setShop_image(rs.getString("shop_image"));
        shopInfo.setShop_phone(rs.getString("shop_phone"));
        shopInfo.setShop_mail(rs.getString("shop_mail"));
        shopInfo.setShop_website(rs.getString("shop_website"));

        shopInfo.setMonday_open(rs.getTime("monday_open"));
        shopInfo.setMonday_close(rs.getTime("monday_close"));
        shopInfo.setTuesday_open(rs.getTime("tuesday_open"));
        shopInfo.setTuesday_close(rs.getTime("tuesday_close"));
        shopInfo.setWednesday_open(rs.getTime("wednesday_open"));
        shopInfo.setWednesday_close(rs.getTime("wednesday_close"));
        shopInfo.setThursday_open(rs.getTime("thursday_open"));
        shopInfo.setThursday_close(rs.getTime("thursday_close"));
        shopInfo.setFriday_open(rs.getTime("friday_open"));
        shopInfo.setFriday_close(rs.getTime("friday_close"));
        shopInfo.setSaturday_open(rs.getTime("saturday_open"));
        shopInfo.setSaturday_close(rs.getTime("saturday_close"));


        return shopInfo;

    }

    private  List<shopAmenrities_entity> findshopAmenrities(Long shopId){
        String SQL = "SELECT * FROM `shop_amenitie` WHERE shop_id =? ";
        return  jdbcTemplate.query(SQL,this::mappshopAmenrities,shopId);
    }
    private shopAmenrities_entity mappshopAmenrities(ResultSet rs, int rowNum) throws SQLException {
        shopAmenrities_entity shopAmenritiesEntity = new shopAmenrities_entity();
        shopAmenritiesEntity.setShop_amenities_id(rs.getLong("shop_amenities_id"));
        shopAmenritiesEntity.setShop_id(rs.getLong("shop_id"));
        shopAmenritiesEntity.setAmenities_id(rs.getLong("amenities_id"));

        return shopAmenritiesEntity;
    }
    private  List<shopImage_entity> findImage(Long shopId){
        String SQL = "SELECT * FROM `shop_image` WHERE `shop_id` =? ";
        return  jdbcTemplate.query(SQL,this::mappshopImage,shopId);
    }
    private shopImage_entity mappshopImage(ResultSet rs, int rowNum) throws SQLException {
        shopImage_entity shopImage = new shopImage_entity();
        shopImage.setShop_image_id(rs.getLong("shop_image_id"));
        shopImage.setShop_id(rs.getLong("shop_id"));
        shopImage.setImage_path(rs.getString("image_path"));

        return shopImage;
    }
    private  List<shopService_entity> findService(Long shopId){
        String SQL = "SELECT * FROM `shop_service` WHERE `shop_id` =? ";
        return  jdbcTemplate.query(SQL,this::mappshopService,shopId);
    }
    private shopService_entity mappshopService(ResultSet rs, int rowNum) throws SQLException {
        shopService_entity shopService = new shopService_entity();
        shopService.setShop_service_id(rs.getLong("shop_service_id"));
        shopService.setShop_id(rs.getLong("shop_id"));
        shopService.setService_id(rs.getLong("service_id"));

        return shopService;
    }
    private  List<shop_type> findShopType(Long shopId){
        String SQL = "SELECT * FROM `shop_type` WHERE `shop_id` =? ";
        return  jdbcTemplate.query(SQL,this::mappshopType,shopId);
    }
    private shop_type mappshopType(ResultSet rs, int rowNum) throws SQLException {
        shop_type shopType = new shop_type();
        shopType.setShop_type_id(rs.getLong("shop_type"));
        shopType.setShop_id(rs.getLong("shop_id"));
        shopType.setType_id(rs.getLong("type_id"));

        return shopType;
    }


}
