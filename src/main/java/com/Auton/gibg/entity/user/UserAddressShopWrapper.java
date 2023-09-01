package com.Auton.gibg.entity.user;
import com.Auton.gibg.entity.address.address_entity;
import com.Auton.gibg.entity.shop.*;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserAddressShopWrapper {
    private user_entity user;
//    private address_entity userAddress;
    private shop_entity userShop;

    private List<shopAmenrities_entity> shopAmenrities;
    private List<shopImage_entity> shopImages;
    private List<shopService_entity> shopServices;
}
