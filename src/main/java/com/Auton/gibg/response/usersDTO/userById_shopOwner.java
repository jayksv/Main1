package com.Auton.gibg.response.usersDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class userById_shopOwner {
    private Long user_id;
    private String email;
    private String password;
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
// address
    private String streetAddress;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
//shop information

    private String shop_name;
    private String street_address;
    private String city;
    private String shop_state;
    private String postal_code;
    private String shop_country;
    private BigDecimal shop_latitude;
    private BigDecimal shop_longitude;
    private String  shop_type_name;
    private String shop_image;
    private Time monday_open;
    private Time monday_close;
    private Time tuesday_open;
    private Time tuesday_close;
    private Time wednesday_open;
    private Time wednesday_close;
    private Time thursday_open;
    private Time thursday_close;
    private Time friday_open;
    private Time friday_close;
    private Time saturday_open;
    private Time saturday_close;
    private Time sunday_open;
    private Time sunday_close;



}

