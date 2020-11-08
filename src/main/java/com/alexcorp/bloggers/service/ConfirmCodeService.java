package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.ConfirmCode;
import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.repository.ConfirmCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmCodeService {

    @Autowired
    private ConfirmCodeRepository codeRepository;

    public boolean checkConfirmCode(Integer code, ConfirmCode.ConfirmType type) {
        return codeRepository.existsByCodeAndType(code, type);
    }

    public ConfirmCode getConfirmCode(Integer code, ConfirmCode.ConfirmType type) throws Throwable {
        ConfirmCode confirmCode = codeRepository.findByCodeAndType(code, type);
        if (confirmCode == null) throw new Throwable("CONFIRM_CODE_NOT_FOUND");

        return confirmCode;
    }

    public ConfirmCode generateConfirmCode(User user, ConfirmCode.ConfirmType type) {
        ConfirmCode code = codeRepository.findByUserAndType(user, type);
        if(code != null) return code;

        code = new ConfirmCode();
        code.setCode((int) (Math.random() * 888888 + 111111));
        code.setType(type);
        code.setUser(user);

        return codeRepository.save(code);
    }

    public void removeConfirmCode(ConfirmCode code) {
        codeRepository.delete(code);
    }
}
