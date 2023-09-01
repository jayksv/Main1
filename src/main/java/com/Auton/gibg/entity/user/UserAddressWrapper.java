package com.Auton.gibg.entity.user;
import com.Auton.gibg.entity.address.address_entity;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserAddressWrapper {
    private user_entity user;
    private address_entity userAddress;
}
