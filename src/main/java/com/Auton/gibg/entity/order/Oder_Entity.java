package com.Auton.gibg.entity.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tb_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Oder_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderID")
    private Long orderID;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "status_id")
    private Long status_id;

    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "created_at")
    private Date created_at;
}
