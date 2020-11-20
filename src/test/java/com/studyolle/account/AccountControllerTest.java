package com.studyolle.account;

import com.studyolle.WithAccount;
import com.studyolle.domain.Account;
import com.studyolle.mail.EmailMessage;
import com.studyolle.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면 보이는지 테스트")
    @Test
    void singUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(model().attributeExists("signupForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 입력 값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "soyo")
                .param("email", "email....")
                .param("password","12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @MockBean
    EmailService emailService;

    @DisplayName("회원가입 처리 - 입력 값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "soyo")
                .param("email", "email@email.com")
                .param("password","12345asdfff")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("soyo"));

        Account account = accountRepository.findByEmail("email@email.com");
        assertNotNull(account);
        //인코딩이 제대로 됐다면 입력한 비밀번호 값과 등록된 비밀번호 값이 달라야 한다.
        assertNotEquals(account.getPassword(), "12345asdfff");
        //가입한 메일이 제대로 등록되었는지
        assertTrue(accountRepository.existsByEmail("email@email.com"));
        //메일 보내는지
        then(emailService).should().sendEmail(any(EmailMessage.class));
        //토큰값이 잘 생성됐는지
        assertNotNull(account.getEmailCheckToken());
    }

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "afsjlsdafk123")
                .param("email", "asdfsda@dsfasd.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("kiki@kiki.com")
                .password("1234567890")
                .nickname("kafka")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
        .param("token", newAccount.getEmailCheckToken())
        .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("kafka"));
    }

    @DisplayName("비밀번호 없이 로그인 - GET")
    @WithAccount("soyo")
    @Test
    void test_email_login_form() throws Exception {
        String viewName = "/email-login";
        mockMvc.perform(get(viewName))
                .andExpect(status().isOk())
                .andExpect(view().name("account"+viewName))
                .andExpect(authenticated());
    }

//    @DisplayName("비밀번호 없이 로그인 - POST")
//    @WithAccount("soyo")
//    @Test
//    void test_email_login_success() throws Exception {
//        Account soyo = accountRepository.findByNickname("soyo");
//
//        mockMvc.perform(post("/email-login")
//                .param("email", soyo.getEmail())
//                .with(csrf()))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(model().attribute("error","이메일 로그인은 한 시간에 한 번만 이용할 수 있습니다."))
//                .andExpect(redirectedUrl("/email-login"));
//    }

}
