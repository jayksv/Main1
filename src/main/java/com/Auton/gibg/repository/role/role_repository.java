package com.Auton.gibg.repository.role;

import com.Auton.gibg.entity.roleEntity.roleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface role_repository extends JpaRepository<roleEntity, Long> {
}
