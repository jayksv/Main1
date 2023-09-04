package com.Auton.gibg.response.usersDTO;

import com.Auton.gibg.response.shopService.shopAmenitiesDTO;
import com.Auton.gibg.response.shopService.shopServiceDTO;
import com.Auton.gibg.response.shopService.shopTypeDTO;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Auton.gibg.entity.shop.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class shopOwner_DTO {
    private String message;

    private shopInfo_DTO shopInfo;

    private List<shopAmenitiesDTO> shopAmenrities;
    private List<shopImage_entity> shopImages;
    private List<shopServiceDTO> shopServices;
    private List<shopTypeDTO> shopType;

}


