package com.Auton.gibg.response.shopDTO;

import com.Auton.gibg.entity.product.color_entity;
import com.Auton.gibg.entity.product.product_image_entity;
import com.Auton.gibg.entity.product.size_entity;
import com.Auton.gibg.entity.shop.shopAmenrities_entity;
import com.Auton.gibg.entity.shop.shopService_entity;
import com.Auton.gibg.entity.shop.shop_type;
import com.Auton.gibg.response.product.productDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class shopTypeAmenityImageServiceDTO {
    private String message;
    private shopAllDTO shop;
    private List<shop_type> types;
    private List<shopAmenrities_entity> amenity;
    private List<shopService_entity> service;
}
