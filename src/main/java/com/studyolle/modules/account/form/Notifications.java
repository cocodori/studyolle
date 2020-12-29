package com.studyolle.modules.account.form;

import lombok.*;

@Getter @Setter
public class Notifications {
    private boolean studyCreatedByEmail;    //스터티 생성 알림

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;   //스터디 가입 신청 결과

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    /*
    *   ModelMapper를 이용해서 컨트롤러에서 처리
    * */
//    public Notifications(Account account) {
//        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
//        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
//        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
//        this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
//        this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
//        this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
//    }
}
