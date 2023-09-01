package com.Auton.gibg.entity.shop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shop_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class shopImage_entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_image_id")
    private Long shop_image_id;

    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "image_path")
    private String image_path;
}
