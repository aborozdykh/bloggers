package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BusinessSignupDto extends UserSignupDto{

    private @NotNull String companyName;
    private @NotNull String webSiteLink;
    private @NotNull String instLogin;
    private @NotNull String facebookLogin;

}
