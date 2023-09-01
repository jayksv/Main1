package com.Auton.gibg.response.usersDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class userById_subadminDTO {
    private Long user_id;
    private String email;
    private String first_name;
    private String last_name;
    private Date birthdate;
    private String gender;
    private String profile_picture;
    private Date created_at;
    private Date last_login;
    private String is_active;
    private String bio;
    private String role_name;

    private String streetAddress;
    private String state;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;


}
