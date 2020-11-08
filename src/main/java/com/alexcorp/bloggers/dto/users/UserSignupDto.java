package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UserSignupDto {

    @Email
    private @NotNull String email;
    private @NotNull String phone;

    private @NotNull String name;
    private @NotNull String surname;

    private @NotNull Date birthDate;
    private @NotNull String city;

}
