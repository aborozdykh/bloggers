package com.alexcorp.bloggers.domain.users;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.users.BusinessSignupDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "BUSINESS")
public class Business extends User {

    private String companyName;
    private String webSiteLink;
    private String instLogin;
    private String facebookLogin;

    public Business(BusinessSignupDto userDto) {
        super(userDto);

        companyName = userDto.getCompanyName();
        webSiteLink = userDto.getWebSiteLink();
        instLogin = userDto.getInstLogin();
        facebookLogin = userDto.getFacebookLogin();
    }
}
