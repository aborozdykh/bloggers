package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginUserDto {

    private @NotNull String login;
    private @NotNull String password;

}
