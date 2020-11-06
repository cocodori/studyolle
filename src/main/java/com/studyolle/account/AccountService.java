package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignupForm signupForm) {
        //회원 등록
        Account newAccount = saveNewAccount(signupForm);
        //메일로 보낼 토큰을 생성하는 메서드
        newAccount.generateEmailCheckToken();
        //이메일로 토큰 보내고 확인
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(SignupForm signupForm) {
        //TODO 회원가입 처리
        //입력 받은 값으로 회원 도메인 초기화
        Account newAccount = Account.builder()
                .email(signupForm.getEmail())
                .nickname(signupForm.getNickname())
                .password(signupForm.getPassword()) //TODO 패스워드 인코딩 필요
                .studyCreatedByWeb(true)
                .studyCreatedByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        //회원 등록
        accountRepository.save(newAccount);
        return newAccount;
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        //메일 작업을 처리할 객체
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("스터디올래 회원 인증 메일"); //이메일 제목
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail() );  //본문
        //메일 전송
        javaMailSender.send(mailMessage);
    }
}
