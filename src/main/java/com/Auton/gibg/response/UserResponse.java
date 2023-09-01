package com.Auton.gibg.response;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String message;
    private UserDto user;

}

