package com.Auton.gibg.entity.shop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shop_amenitie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class shopAmenrities_entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_amenities_id")
    private Long shop_amenities_id;

    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "amenities_id")
    private Long amenities_id;
}
