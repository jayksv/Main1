package com.Auton.gibg.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Auton.gibg.entity.product.color_entity;

import java.util.List;

@Repository
public interface color_repository extends JpaRepository<color_entity, Long> {
//    List<color_entity> findByProductId(Long product_id);
}
