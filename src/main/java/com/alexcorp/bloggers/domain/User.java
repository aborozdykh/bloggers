package com.alexcorp.bloggers.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class User implements Serializable {

    @Id
    private String id;

    private String email;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    protected Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    protected Active active = Active.NonActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;


    public enum Role implements GrantedAuthority{

        ADMIN;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    public enum Active {
        NonActive, Active
    }
}

