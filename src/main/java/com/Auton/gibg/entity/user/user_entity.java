package com.Auton.gibg.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Table(name = "tb_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class user_entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "birthdate")
    private Date birthdate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "profile_picture")
    private String profile_picture;

    @Column(name = "is_active",columnDefinition = "bigint default Active")
    private String is_active;

    @Column(name = "bio")
    private String bio;

    @Column(name = "role_id", columnDefinition = "bigint default 4")
    private Long role_id;

    @Column(name = "address_id")
    private Long address_id;

    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "phone")
    private String phone;



};

