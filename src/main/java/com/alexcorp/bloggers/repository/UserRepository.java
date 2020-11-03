package com.alexcorp.bloggers.repository;

import com.alexcorp.bloggers.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByTelNumEndingWith(String telNum);

}
