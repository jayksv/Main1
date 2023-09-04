package com.Auton.gibg.response.usersDTO;

import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Auton.gibg.entity.address.address_entity;
import com.Auton.gibg.entity.shop.*;
import com.Auton.gibg.response.usersDTO.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class shopOwner_DTO {
    private String message;

    private shopInfo_DTO shopInfo;

    private List<shopAmenrities_entity> shopAmenrities;
    private List<shopImage_entity> shopImages;
    private List<shopService_entity> shopServices;
    private List<shop_type> shopType;

}


