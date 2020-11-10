package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    /*
    *   @WithAccount를 이용해서 만든 계정을 테스트가 끝나면 지워야 한다.
    * */
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("프로필 수정 폼(Get요청)")
    @WithAccount("soyo")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "짧은 소개";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_VIEW_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @DisplayName("프로필 수정 - 입력값 정상")
    @WithAccount("soyo")
    @Test
    void updateProfile() throws Exception {
        String bio = "test intro";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_VIEW_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_VIEW_URL))
                .andExpect(flash().attributeExists("message"));

        Account soyo = accountRepository.findByNickname("soyo");

        assertEquals(bio, soyo.getBio());
    }

    @DisplayName("프로필 수정 - 입력값 오류")
    @WithAccount("soyo")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "dslkfajsladfjldsajfklwejfhkjawefhasjkfdhkjahuifewhfiusahfkjsdhfkjwihehfuewhfkweuffsdahfksdahfakjsd";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_VIEW_URL)
        .param("bio", bio)
        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account soyo = accountRepository.findByNickname("soyo");
        assertNotNull(soyo.getBio());


    }



}