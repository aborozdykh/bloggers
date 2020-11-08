package com.alexcorp.bloggers.domain;

import com.alexcorp.bloggers.dto.users.UserSignupDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "USR")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    protected Long id;

    @JsonIgnore
    protected String oauthId;

    @Column(length = 64)
    protected String email;
    @Column(length = 16)
    protected String phone;

    @JsonIgnore
    @Column(length = 128)
    protected String password;

    @Column(length = 32)
    protected String name;
    @Column(length = 32)
    protected String surname;

    @Temporal(TemporalType.DATE)
    protected Date birthDate;

    protected String city;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    protected Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    protected Active active = Active.NON_ACTIVE;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime lastLogin;

    public User(UserSignupDto userDto) {
        email = userDto.getEmail();
        phone = userDto.getPhone();
        name = userDto.getName();
        surname = userDto.getSurname();
        birthDate = userDto.getBirthDate();
        city = userDto.getCity();
    }

    public enum Role implements GrantedAuthority{

        BLOGGER, BUSINESS, MODER, ADMIN;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    public enum Active {
        NON_ACTIVE, ACTIVE, BANNED
    }
}

