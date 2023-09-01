package com.Auton.gibg.entity.vehicle;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_vehicle_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class vechcleType_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vtype_id")
    private Long vehicle_id ;

    @Column(name = "v_name", nullable = false)
    private String vehicle_name;

}
