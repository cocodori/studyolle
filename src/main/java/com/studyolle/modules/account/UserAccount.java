package com.studyolle.modules.account;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/*
*   로그인할 때 사용하는 AccountService의 login()에는 account라는 프로퍼티가 없으므로
*   중간 역할을 할 account프로퍼티를 가진 클래스
*   스프링 시큐리티가 가진 유저 정보와, 도메인이 가진 유저 정보 사이의 갭을 매꾸는 일종의 어댑터 같은 역할.
* */

@Getter
public class UserAccount extends User {
    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_UESR")));
        this.account = account;
    }
}
