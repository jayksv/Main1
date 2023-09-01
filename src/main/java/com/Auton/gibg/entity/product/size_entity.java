package com.Auton.gibg.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_size")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class size_entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Long size_id;

    @Column(name = "product_id")
    private Long product_id;

    @Column(name = "size_name")
    private String size_name;
}
