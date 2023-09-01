package com.Auton.gibg.response.usersDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class usersAllDTO {
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
    private String street_address;
    private String role_name;
    private String shop_name;


}
