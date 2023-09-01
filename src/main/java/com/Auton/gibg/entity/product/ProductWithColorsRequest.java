package com.Auton.gibg.entity.product;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Auton.gibg.entity.product.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductWithColorsRequest {
    private product_entity product;
    private List<color_entity> colors;
    private List<size_entity> sizes;
    private List<product_image_entity> product_images;
}
