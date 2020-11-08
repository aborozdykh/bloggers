package com.alexcorp.bloggers.repository;

import com.alexcorp.bloggers.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    public User findByEmail(String email);
    public User findByPhoneEndingWith(String phone);
    public User findByOauthId(String oauthId);

    public boolean existsByEmailOrPhoneEndingWith(String email, String phone);

    boolean existsByEmail(String email);

    boolean existsByOauthId(String sub);
}
