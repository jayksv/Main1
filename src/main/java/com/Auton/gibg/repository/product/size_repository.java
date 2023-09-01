package com.Auton.gibg.repository.product;

import com.Auton.gibg.entity.product.size_entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface size_repository extends JpaRepository<size_entity, Long> {
//    @Query("SELECT s FROM size_entity s WHERE s.product_id = :productId")
//    List<size_entity> findByProductId(@Param("productId") Long productId);
}

