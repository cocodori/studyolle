package com.studyolle.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

/*
*   콘솔에 토큰을 출력하는 가짜 객체
*   TODO 나중에 진짜 객체로 변경해야 함
* */
@Slf4j
@Profile({"local", "dev"})   //local환경일 때 이 빈이 등록된다.
@Component
public class ConsoleMailSender implements JavaMailSender {
    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream inputStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {

    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {


    }

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        log.info(simpleMailMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

    }
}
