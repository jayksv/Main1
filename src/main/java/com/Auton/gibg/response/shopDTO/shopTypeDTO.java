package com.Auton.gibg.response.shopDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class shopTypeDTO {
    private Long shop_type_id;
    private Long shop_id;
    private String type_id;
    private Date create_at;
}
