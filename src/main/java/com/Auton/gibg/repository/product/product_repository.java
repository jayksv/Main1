package com.Auton.gibg.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Auton.gibg.entity.product.product_entity;
import org.springframework.stereotype.Repository;

@Repository
public interface product_repository extends JpaRepository<product_entity,Long> {

}
