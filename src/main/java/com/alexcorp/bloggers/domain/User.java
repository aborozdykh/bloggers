package com.alexcorp.bloggers.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "USR")
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    protected Long id;

    @Column(unique = true, length = 16)
    protected String telNum;

    @JsonIgnore
    @Column(length = 64)
    protected String password;

    @Column(length = 64)
    protected String fullName;

    @Temporal(TemporalType.DATE)
    protected Date birthDate;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    protected Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    protected Active active = Active.NonActive;

    @Temporal(TemporalType.DATE)
    private Date lastLogin;


    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return telNum;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return active.equals(Active.Active);
    }

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

