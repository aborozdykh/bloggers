package com.alexcorp.bloggers.dto.users;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BusinessSignupDto extends UserSignupDto{

    private @NotNull String companyName;
    private String webSiteLink;
    private String instLogin;
    private String facebookLogin;

}
