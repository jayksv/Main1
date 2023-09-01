package com.Auton.gibg.entity.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class vehicleDTO {
    private Long vehicleId;
    private String model;
    private Integer year;
    private String color;
    private  String type;

}
