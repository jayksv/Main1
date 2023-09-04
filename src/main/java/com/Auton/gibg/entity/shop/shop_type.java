package com.Auton.gibg.entity.shop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "shop_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class shop_type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_type_id")
    private Long shop_type_id;

    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "type_id")
    private Long type_id;

    @Column(name = "create_at")
    private Date create_at;
}
