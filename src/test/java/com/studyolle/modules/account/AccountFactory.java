package com.studyolle.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account cocoboy = new Account();
        cocoboy.setNickname(nickname);
        cocoboy.setEmail(nickname + "@email.com");
        accountRepository.save(cocoboy);

        return cocoboy;
    }

}
