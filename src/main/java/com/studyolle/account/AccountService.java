package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.settings.Notifications;
import com.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
    *  지금 이 객체는 디태치드 객체여서 DB에 싱크가 되지 않았다. 그러니 DB에 저장이 되지 않고 Token값이 Null인 것이다.
    *  saveNewAccount에서 return한 객체는, save()안에서는 트랜잭션이어서 해당 엔티티가 퍼시스턴트 상태다.
    *   하지만 Return된 다음에는 디태치먼트 상태다. 트랜잭션 범위를 벗어났기 때문이다.
    *   트랜잭션 안에 있을 때에만 퍼시스턴트 상태가 되어 DB에 싱크할 수 있다.
    *   따라서 @Transactional 애노테이션을 붙여서 디태치드 상태가 아닌 퍼시스턴트 상태를 유지할 수 있도록 해야만
    *   token값을 원하는 대로 DB에 저장할 수 있다.
    */
    public Account processNewAccount(SignupForm signupForm) {
        //회원 등록
        Account newAccount = saveNewAccount(signupForm);
        //메일로 보낼 토큰을 생성하는 메서드
        newAccount.generateEmailCheckToken();
        //이메일로 토큰 보내고 확인
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignupForm signupForm) {
        //입력 받은 값으로 회원 도메인 초기화
        Account newAccount = Account.builder()
                .email(signupForm.getEmail())
                .nickname(signupForm.getNickname())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        //회원 등록
        accountRepository.save(newAccount);
        return newAccount;
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        //메일 작업을 처리할 객체
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("스터디올래 회원 인증 메일"); //이메일 제목
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail() );  //본문
        //메일 전송
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
//        account.setUrl(profile.getUrl());
//        account.setOccupation(profile.getOccupation());
//        account.setLocation(profile.getLocation());
//        account.setBio(profile.getBio());
//        account.setProfileImage(profile.getProfileImage());
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
//        account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
//        account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
//        account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
//        account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
//        account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }
}
