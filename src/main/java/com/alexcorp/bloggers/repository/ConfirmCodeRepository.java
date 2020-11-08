package com.alexcorp.bloggers.repository;

import com.alexcorp.bloggers.domain.ConfirmCode;
import com.alexcorp.bloggers.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmCodeRepository extends JpaRepository<ConfirmCode, String> {

    boolean existsByCodeAndType(Integer code, ConfirmCode.ConfirmType type);

    ConfirmCode findByCodeAndType(Integer code, ConfirmCode.ConfirmType type);

    ConfirmCode findByUserAndType(User user, ConfirmCode.ConfirmType type);
}
