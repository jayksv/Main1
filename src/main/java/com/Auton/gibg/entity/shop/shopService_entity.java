package com.Auton.gibg.entity.shop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shop_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class shopService_entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_service_id")
    private Long shop_service_id;

    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "service_id")
    private Long service_id;

}
