package com.Auton.gibg.repository.category;

import com.Auton.gibg.entity.category.category_entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface category_repository extends JpaRepository<category_entity, Long> {
//    List<category_entity> findByParentCategoryId(Long parentCategoryId);
}

