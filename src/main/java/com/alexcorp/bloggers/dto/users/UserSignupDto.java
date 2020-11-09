package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserSignupDto {

    @Email
    private @NotNull String email;
    private @NotNull String phone;
    @Size(min = 1)
    private @NotNull String password;

    private @NotNull String name;
    private @NotNull String surname;

    private @NotNull Date birthDate;
    private @NotNull String city;

}
