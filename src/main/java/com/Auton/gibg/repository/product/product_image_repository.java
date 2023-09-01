package com.Auton.gibg.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Auton.gibg.entity.product.*;

import java.util.List;

@Repository
public interface product_image_repository extends JpaRepository<product_image_entity,Long> {

//    List<product_image_entity> findByProductId(Long product_id);
}
