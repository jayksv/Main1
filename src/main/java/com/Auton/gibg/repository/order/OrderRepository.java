package com.Auton.gibg.repository.order;

import com.Auton.gibg.entity.order.Oder_Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Oder_Entity, Long> {
}
