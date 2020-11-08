package com.studyolle.main;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignupForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class MainControllerTest {
    /*
    *   JUnit5는 Dependency Injection을 지원하는데, 타입이 정해져있다.
    *   생성자를 사용하는 방법인, @RequiredArg...를 이용하는 생성자 주입은 원하는 객체를 주입할 수 없다.
    *   JUnit이 먼저 개입해서 다른 인스턴스를 넣으려고 하기 때문이다.
    * */
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void addAccount() {
        SignupForm signupForm = new SignupForm();
        signupForm.setNickname("soyo");
        signupForm.setEmail("soyo@email.com");
        signupForm.setPassword("12345678");
        accountService.processNewAccount(signupForm);
    }

    /*
    *   BeforeEach는 모든 테스트 실행 전에 한 번씩 실행된다.
    *   따라서 전체 테스트를 진행할 때 중복 값이 들어가서 충돌이 일어날 수 있으므로
    *   테스트를 종료할 때마다 모든 데이터를 지워준다.
    * */
    @AfterEach
    void deleteAccount () {
        accountRepository.deleteAll();
    }

    @DisplayName("로그인 테스트 - 이메일 로그인")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login")
        .param("username", "soyo@email.com")
        .param("password", "12345678")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("soyo"));
        //이메일이 아닌 유저이름으로 로그인이 되는 이유는
        //UserAccount클래스에서 유저네임 부분을 닉네임으로 리턴했기 때문이다.
    }

    @DisplayName("로그인 테스트 - 닉네임 로그인")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login")
        .param("username", "soyo")
        .param("password", "12345678")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("soyo"));
    }

    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
        .param("username", "yoyo")
        .param("password", "12345678")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃")
    @WithMockUser   //임의로 로그인된 사자
    @Test
    void test_logout() throws Exception {
        mockMvc.perform(post("/logout")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }


}