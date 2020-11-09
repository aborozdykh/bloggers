package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginUserDto {

    @NotNull
    private String login;

    @NotNull
    @Size(min = 1)
    private  String password;

}
