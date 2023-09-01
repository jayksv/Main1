package com.Auton.gibg.controller.product;



import com.Auton.gibg.entity.product.ProductWithColorsRequest;
import com.Auton.gibg.middleware.authToken;
import com.Auton.gibg.response.product.ProductWithColorsSizesAndImagesResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.Auton.gibg.repository.product.*;
import com.Auton.gibg.entity.product.*;
import com.Auton.gibg.response.*;
import com.Auton.gibg.response.product.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class product_controller {
    @Value("${jwt_secret}")
    private String jwt_secret;
    private final JdbcTemplate jdbcTemplate;
    private final authToken authService;
    @Autowired
    private product_repository productRepository;
    @Autowired
    private color_repository colorRepository;

    @Autowired
    private size_repository sizeRepository;
    @Autowired
    private product_image_repository productImageRepository;

    public product_controller(JdbcTemplate jdbcTemplate,  authToken authService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authService = authService;
    }


    @PostMapping("/product/insert")
    public ResponseEntity<ResponseWrapper<String>> insertProduct(@RequestBody ProductWithColorsRequest request,
                                                                 @RequestHeader("Authorization") String authorizationHeader) {
        try {
            product_entity product = request.getProduct();
            List<color_entity> colors = request.getColors();
            List<size_entity> sizes = request.getSizes();
            List<product_image_entity> productAnyImages = request.getProduct_images();


            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            // Check if required fields are not null and price is greater than 0
            if (product.getProduct_name() == null || product.getPrice() == null || product.getStock_quantity() == -1 ) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Product name, price, and stock quantity cannot be null. Price must be greater than 0.", null);
                return ResponseEntity.badRequest().body(responseWrapper);
            }
            if ( product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Price must be greater than 0.", null);
                return ResponseEntity.badRequest().body(responseWrapper);
            }





            String token = authorizationHeader.substring("Bearer ".length());

            Claims claims = Jwts.parser()
                    .setSigningKey(jwt_secret) // Replace with your secret key
                    .parseClaimsJws(token)
                    .getBody();
            // Extract necessary claims (you can add more as needed)
            Long authenticatedUserId = claims.get("user_id", Long.class);


            // Save the product using the repository
            product.setUser_id(authenticatedUserId);
            productRepository.save(product);

            // Set the product_id in each color_entity and associate them with the product
            for (color_entity color : colors) {
                color.setProduct_id(product.getProduct_id());
            }
            for(size_entity size:sizes){
                size.setProduct_id(product.getProduct_id());
            }

            for(product_image_entity imageOne:productAnyImages){
                imageOne.setProduct_id(product.getProduct_id());
            }

            // Save the colors using the colorRepository
            colorRepository.saveAll(colors);
            sizeRepository.saveAll(sizes);
            productImageRepository.saveAll(productAnyImages);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Product and colors inserted successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while inserting the product and colors", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @GetMapping("/product/all")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }



            String sql = "SELECT tb_products.product_id, tb_products.description, tb_products.product_name, tb_products.price, tb_products.stock_quantity, tb_products.user_id, tb_products.shope_id, tb_products.category_id, tb_products.product_image, tb_products.created_at, tb_categories.category_name, tb_users.first_name, tb_users.last_name, CONCAT(tb_users.first_name, ' ', tb_users.last_name) AS full_name, tb_shop.shop_name FROM `tb_products` JOIN tb_categories ON tb_products.category_id = tb_categories.category_id JOIN tb_users ON tb_products.user_id = tb_users.user_id JOIN tb_shop ON tb_products.shope_id = tb_shop.shop_id;";
            List<productDTO> products = jdbcTemplate.query(sql, this::mapProductRow);

            List<productColorSizeImageDTO> responses = new ArrayList<>();
            for (productDTO product : products) {
                List<color_entity> colors = findColorsByProductId(product.getProduct_id());
                List<size_entity> sizes = findSizesByProductId(product.getProduct_id());
                List<product_image_entity> productImages = findProductImagesByProductId(product.getProduct_id());

                productColorSizeImageDTO response = new productColorSizeImageDTO("Success", product, colors, sizes, productImages);
                responses.add(response);
            }

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/product/findById/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId,  @RequestHeader("Authorization") String authorizationHeader) {
        try {

            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            String sql = "SELECT tb_products.product_id, tb_products.description, tb_products.product_name, tb_products.price, tb_products.stock_quantity, tb_products.user_id, tb_products.shope_id, tb_products.category_id, tb_products.product_image, tb_products.created_at, tb_categories.category_name, tb_users.first_name, tb_users.last_name, CONCAT(tb_users.first_name, ' ', tb_users.last_name) AS full_name, tb_shop.shop_name FROM `tb_products` JOIN tb_categories ON tb_products.category_id = tb_categories.category_id JOIN tb_users ON tb_products.user_id = tb_users.user_id JOIN tb_shop ON tb_products.shope_id = tb_shop.shop_id WHERE tb_products.product_id = ?";
            productDTO product = jdbcTemplate.queryForObject(sql, new Object[]{productId}, this::mapProductRow);
            if (product == null) {
                ResponseWrapper<String> notFoundResponse = new ResponseWrapper<>("Product not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }
            List<color_entity> colors = findColorsByProductId(productId);
            List<size_entity> sizes = findSizesByProductId(productId);
            List<product_image_entity> productImages = findProductImagesByProductId(productId);

            productColorSizeImageDTO response = new productColorSizeImageDTO("Success", product, colors, sizes, productImages);

            return ResponseEntity.ok(response);
        }catch (EmptyResultDataAccessException e) {
            ResponseWrapper<String> notFoundResponse = new ResponseWrapper<>("Product not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/product/byShop/{shopeId}")
    public ResponseEntity<?> getProductsByShop(@PathVariable Long shopeId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            String sql = "SELECT tb_products.product_id, tb_products.description, tb_products.product_name, tb_products.price, tb_products.stock_quantity, tb_products.user_id, tb_products.shope_id, tb_products.category_id, tb_products.product_image, tb_products.created_at, tb_categories.category_name, tb_users.first_name, tb_users.last_name, CONCAT(tb_users.first_name, ' ', tb_users.last_name) AS full_name, tb_shop.shop_name FROM `tb_products` JOIN tb_categories ON tb_products.category_id = tb_categories.category_id JOIN tb_users ON tb_products.user_id = tb_users.user_id JOIN tb_shop ON tb_products.shope_id = tb_shop.shop_id WHERE tb_products.shope_id = ?";
            List<productDTO> products = jdbcTemplate.query(sql, new Object[]{shopeId}, this::mapProductRow);

            if (products.isEmpty()) {
                ResponseWrapper<String> notFoundResponse = new ResponseWrapper<>("No products found for the specified shop.", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            List<productColorSizeImageDTO> responses = new ArrayList<>();
            for (productDTO product : products) {
                List<color_entity> colors = findColorsByProductId(product.getProduct_id());
                List<size_entity> sizes = findSizesByProductId(product.getProduct_id());
                List<product_image_entity> productImages = findProductImagesByProductId(product.getProduct_id());

                productColorSizeImageDTO response = new productColorSizeImageDTO("Success", product, colors, sizes, productImages);
                responses.add(response);
            }

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/product/update/{productId}")
    public ResponseEntity<ResponseWrapper<String>> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductWithColorsRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<ResponseWrapper<Void>> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<Void> authResponseBody = authResponse.getBody();
                return ResponseEntity.status(authResponse.getStatusCode()).body(new ResponseWrapper<>(authResponseBody.getMessage(), null));
            }

            product_entity existingProduct = productRepository.findById(productId).orElse(null);
            if (existingProduct == null) {
                ResponseWrapper<String> notFoundResponse = new ResponseWrapper<>("Product not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            // Check if required fields are not null and price is greater than 0
            if (request.getProduct().getProduct_name() == null || request.getProduct().getPrice() == null || request.getProduct().getStock_quantity() == -1 ) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Product name, price, and stock quantity cannot be null. Price must be greater than 0.", null);
                return ResponseEntity.badRequest().body(responseWrapper);
            }
            if ( request.getProduct().getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Price must be greater than 0.", null);
                return ResponseEntity.badRequest().body(responseWrapper);
            }


            // Update the product fields
            product_entity updatedProduct = request.getProduct();
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setProduct_name(updatedProduct.getProduct_name());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setStock_quantity(updatedProduct.getStock_quantity());
            existingProduct.setProduct_image(updatedProduct.getProduct_image());

            productRepository.save(existingProduct);

            // Update colors using JDBC
            List<color_entity> updatedColors = request.getColors();
            for (color_entity updatedColor : updatedColors) {
                String updateColorSql = "UPDATE product_color SET color_name = ? WHERE product_id = ?";
                jdbcTemplate.update(updateColorSql, updatedColor.getColor_name(), productId);

            }

            // Update sizes using JDBC
            List<size_entity> updatedSizes = request.getSizes();
            for (size_entity updatedSize : updatedSizes) {
                String updateSizeSql = "UPDATE product_size SET size_name = ? WHERE product_id = ?";
                jdbcTemplate.update(updateSizeSql, updatedSize.getSize_name(), productId);
            }

            // Update product images using JDBC
            List<product_image_entity> updatedProductImages = request.getProduct_images();
            for (product_image_entity updatedImage : updatedProductImages) {
                String updateImageSql = "UPDATE product_images SET product_image_path = ? WHERE product_id = ?";
                jdbcTemplate.update(updateImageSql, updatedImage.getProduct_image_path(), productId);
            }

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Product and associated entities updated successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while updating the product and associated entities", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }

    @DeleteMapping("/product/delete/{productId}")
    public ResponseEntity<ResponseWrapper<String>> deleteProduct(@PathVariable Long productId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Validate authorization using authService
            ResponseEntity<?> authResponse = authService.validateAuthorizationHeader(authorizationHeader);
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Token is invalid.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseWrapper);
            }

            // Check if the product exists
            product_entity existingProduct = productRepository.findById(productId).orElse(null);
            if (existingProduct == null) {
                ResponseWrapper<String> notFoundResponse = new ResponseWrapper<>("Product not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            // Delete product and associated entities using SQL
            String deleteProductSql = "DELETE FROM tb_products WHERE product_id = ?";
            jdbcTemplate.update(deleteProductSql, productId);

            String deleteColorsSql = "DELETE FROM product_color WHERE product_id = ?";
            jdbcTemplate.update(deleteColorsSql, productId);

            String deleteSizesSql = "DELETE FROM product_size WHERE product_id = ?";
            jdbcTemplate.update(deleteSizesSql, productId);

            String deleteImagesSql = "DELETE FROM product_images WHERE product_id = ?";
            jdbcTemplate.update(deleteImagesSql, productId);

            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("Product and associated entities deleted successfully", null);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>("An error occurred while deleting the product and associated entities", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        }
    }



    // function of product
    private productDTO mapProductRow(ResultSet rs, int rowNum) throws SQLException {
        productDTO product = new productDTO();
        product.setProduct_id(rs.getLong("product_id"));
        product.setDescription(rs.getString("description"));
        product.setProduct_name(rs.getString("product_name"));
        product.setPrice(rs.getDouble("price"));
        product.setStock_quantity(rs.getLong("stock_quantity"));
        product.setUser_id(rs.getLong("user_id"));
        product.setShope_id(rs.getLong("shope_id"));
        product.setCategory_id(rs.getLong("category_id"));
        product.setProduct_image(rs.getString("product_image"));
        product.setCreateAt(rs.getDate("created_at"));

        // Additional fields
        product.setCreate_by(rs.getString("full_name"));
        product.setCategory_name(rs.getString("category_name"));
        product.setShop_name(rs.getString("shop_name"));

        return product;
    }

    private List<color_entity> findColorsByProductId(Long productId) {
        String sql = "SELECT * FROM product_color WHERE product_id = ?";
        return jdbcTemplate.query(sql, this::mapColorRow, productId);
    }

    private color_entity mapColorRow(ResultSet rs, int rowNum) throws SQLException {
        color_entity color = new color_entity();
        color.setColor_id(rs.getLong("color_id"));
        color.setColor_name(rs.getString("color_name"));
        color.setProduct_id(rs.getLong("product_id"));

        return color;
    }

    private List<size_entity> findSizesByProductId(Long productId) {
        String sql = "SELECT * FROM product_size WHERE product_id = ?";
        return jdbcTemplate.query(sql, this::mapSizeRow, productId);
    }

    private size_entity mapSizeRow(ResultSet rs, int rowNum) throws SQLException {
        size_entity size = new size_entity();
        size.setSize_id(rs.getLong("size_id"));
        size.setSize_name(rs.getString("size_name"));
       size.setProduct_id(rs.getLong("product_id"));
        return size;
    }

    private List<product_image_entity> findProductImagesByProductId(Long productId) {
        String sql = "SELECT * FROM product_images WHERE product_id = ?";
        return jdbcTemplate.query(sql, this::mapProductImageRow, productId);
    }

    private product_image_entity mapProductImageRow(ResultSet rs, int rowNum) throws SQLException {
        product_image_entity productImage = new product_image_entity();
        productImage.setImage_id(rs.getLong("image_id"));
        productImage.setProduct_image_path(rs.getString("product_image_path"));
       productImage.setProduct_id(rs.getLong("product_id"));
        return productImage;
    }



}
