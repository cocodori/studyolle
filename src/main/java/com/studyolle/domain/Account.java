package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of="id")
@Setter
@Getter
@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;
    //이메일 인증을 받은 사용자
    private boolean emailVerified;
    //인증 메일 토큰
    private String emailCheckToken;

    private LocalDateTime joinedAt;
    //계정의 프로필
    private String bio;
    //자신의 웹사이트 url
    private String url;
    //직업
    private String occupation;
    //사는 곳
    private String location;
    /*
    *   @Lob을 선언하면 varchar가 아닌 text타입으로 생성된다.
    *   @Basic(fetch = fetchType.EAGER)
    * */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    //스터디가 새로 만들어졌다는 알림을 어떻게 받을 것인가.(email or web)
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    //스터디 가입 신청 결과를 어떻게 받을 것인가(email or web)
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    //스터디의 갱신된 정보 알림을 어떻게 받을 것인가
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    /*
    *   토큰 값은 랜덤하게 UUID를 이용해서 생성한다.
    * */
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();

    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }
}

