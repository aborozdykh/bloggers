package com.alexcorp.bloggers.domain.users;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.users.BloggerSignupDto;
import com.alexcorp.bloggers.dto.users.UserSignupDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "BLOGGER")
public class Blogger extends User {

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @ElementCollection(targetClass = Language.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "blogger_lang", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Language> langs = new HashSet<>();

    @ElementCollection(targetClass = Specialization.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "blogger_spec", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Specialization> specials = new HashSet<>();

    public Blogger(BloggerSignupDto userDto) {
        super(userDto);

        init(userDto);
    }

    public void init(BloggerSignupDto userDto) {
        sex = userDto.getSex();
        langs = userDto.getLangs();
        specials = userDto.getSpecials();
    }

    public enum Language {
        EN, HE, RU
    }

    public enum Specialization {
        BEAUTY, TECH
    }

    public enum Sex {

        HE, SHE

    }
}
