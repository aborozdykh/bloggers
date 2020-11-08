package com.alexcorp.bloggers.dto.users;

import com.alexcorp.bloggers.domain.users.Blogger;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class BloggerSignupDto extends UserSignupDto {

    private @NotNull Blogger.Sex sex;
    private @NotNull Set<Blogger.Language> langs = new HashSet<>();
    private @NotNull Set<Blogger.Specialization> specials = new HashSet<>();

}
