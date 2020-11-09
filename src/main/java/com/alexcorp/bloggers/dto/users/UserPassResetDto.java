package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserPassResetDto {

    @NotNull
    private Integer code;

    @NotNull
    private String password;

}
