package com.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
/*
* 가입하는 사용자의 이메일이나 닉네임이 이미 사용 중인지 체크한다.
* */
@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignupForm signupForm = (SignupForm)errors;
        if (accountRepository.existsByEmail(signupForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signupForm.getEmail()}, "이미 사용 중인 이메일입니다.");
        }

        if (accountRepository.existsByNickname(signupForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signupForm.getNickname()}, "이미 사용 중인 닉네임입니다.");
        }
    }
}
