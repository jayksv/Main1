package com.Auton.gibg.entity.amenrities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class amenrities_entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amenities_id")
    private Long amenities_id;

    @Column(name = "amenities_name")
    private String amenities_name;

    @Column(name = "amenities_icon")
    private String amenities_icon;
}
