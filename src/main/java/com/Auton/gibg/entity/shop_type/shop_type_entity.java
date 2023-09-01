package com.Auton.gibg.entity.shop_type;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_shop_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class shop_type_entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long type_id ;

    @Column(name = "type_name", nullable = false)
    private String type_name;

    @Column(name = "type_icon")
    private String type_icon;
}
