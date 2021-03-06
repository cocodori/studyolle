package com.studyolle.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.infra.MockMvcTest;
import com.studyolle.modules.account.WithAccount;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.AccountService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.account.form.TagForm;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.studyolle.modules.account.SettingsController.*;

@MockMvcTest
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired TagRepository tagRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired AccountService accountService;
    @Autowired ZoneRepository zoneRepository;
    private Zone testZone = Zone.builder()
            .city("test")
            .localNameOfCity("test local")
            .province("test province")
            .build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    /*
    *   @WithAccount를 이용해서 만든 계정을 테스트가 끝나면 지워야 한다.
    * */
    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @DisplayName("프로필 수정 폼(Get요청)")
    @WithAccount("soyo")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "짧은 소개";
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @DisplayName("프로필 수정 - 입력값 정상")
    @WithAccount("soyo")
    @Test
    void updateProfile() throws Exception {
        String bio = "test intro";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT+SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account soyo = accountRepository.findByNickname("soyo");

        assertEquals(bio, soyo.getBio());
    }

    @DisplayName("프로필 수정 - 입력값 오류")
    @WithAccount("soyo")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "dslkfajsladfjldsajfklwejfhkdsdasjawefhasjkfdhkjahuifewhfiusahfkjsdhfkjwihehfuewhfkweuffsdahfksdahfakjsd";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
        .param("bio", bio)
        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account soyo = accountRepository.findByNickname("soyo");
        assertNull(soyo.getBio());
    }

    @DisplayName("비밀번호 수정 폼")
    @WithAccount("soyo")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(ROOT+SETTINGS+PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @DisplayName("비밀번호 수정 - 입력값 정상")
    @WithAccount("soyo")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(ROOT+SETTINGS+PASSWORD)
                .param("newPassword", "12345678")
                .param("newPassowrdConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT+SETTINGS+PASSWORD))
                .andExpect(flash().attributeExists("message"));
        Account soyo = accountRepository.findByNickname("soyo");
        assertTrue(passwordEncoder.matches("12345678", soyo.getPassword()));
    }

    @DisplayName("비밀번호 수정 - 입력값 에러 - 비밀번호 불일치")
    @WithAccount("soyo")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(ROOT+SETTINGS+PASSWORD)
        .param("newPassword", "12345678")
        .param("newPasswordConfirm", "12345677")
        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS+PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }


    @DisplayName("알림 받기 설정 - GET - 폼")
    @WithAccount("soyo")
    @Test
    void test_notifications_form() throws Exception {
        mockMvc.perform(get(ROOT+SETTINGS+NOTIFICATIONS))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS+NOTIFICATIONS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @DisplayName("알림 받기 설정 - POST - 입력값 정상")
    @WithAccount("soyo")
    @Test
    void test_notifications_update_success () throws Exception {
        mockMvc.perform(post(ROOT+SETTINGS+NOTIFICATIONS)
                .param("studyCreatedByEmail", "true")
                .param("studyCreatedByWeb", "true")
                .param("studyEnrollmentResultByEmail", "true")
                .param("studyEnrollmentResultByWeb", "true")
                .param("studyUpdatedByEmail", "true")
                .param("studyUpdatedByWeb", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT+SETTINGS+NOTIFICATIONS))
                .andExpect(flash().attributeExists("message"));

        Account soyo = accountRepository.findByNickname("soyo");

        assertTrue(soyo.isStudyCreatedByWeb());
        assertTrue(soyo.isStudyCreatedByEmail());
        assertTrue(soyo.isStudyEnrollmentResultByEmail());
        assertTrue(soyo.isStudyEnrollmentResultByWeb());
        assertTrue(soyo.isStudyUpdatedByEmail());
        assertTrue(soyo.isStudyUpdatedByWeb());
    }

    @DisplayName("알림 받기 설정 - POST - 입력값 오류")
    @WithAccount("soyo")
    @Test
    void test_notifications_update_fail () throws Exception {
        //boolean이 아니라 다른 타입이 들어오는 경우
        mockMvc.perform(post(ROOT+SETTINGS+NOTIFICATIONS)
                .param("studyCreatedByEmail", "asdsadasdf")
                .param("studyCreatedByWeb", "21312")
                .param("studyEnrollmentResultByEmail", "asdsadasdf")
                .param("studyEnrollmentResultByWeb", "asdsadasdf")
                .param("studyUpdatedByEmail", "asdsadasdf")
                .param("studyUpdatedByWeb", "asdsadasdf")
                .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("닉네임 수정 폼")
    @WithAccount("soyo")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(ROOT+SETTINGS+ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @DisplayName("닉네임 수정 - 입력값 정상")
    @WithAccount("soyo")
    @Test
    void test_updateAccount_success() throws Exception {
        String newNickname = "cocoboy";
        mockMvc.perform(post(ROOT+SETTINGS+ACCOUNT)
        .param("nickname", newNickname)
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT+SETTINGS+ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.existsByNickname(newNickname));
    }

    @DisplayName("닉네임 수정 - 입력 값 에러")
    @WithAccount("soyo")
    @Test
    void updateAccount_fail() throws Exception {
                String newNickname = "¯\\\\_(ツ)_/¯";
                mockMvc.perform(post(ROOT+SETTINGS+ACCOUNT)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS+ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @DisplayName("태그 수정 폼")
    @WithAccount("soyo")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT+SETTINGS+TAGS))
                .andExpect(view().name(SETTINGS+TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @DisplayName("계정에 태그 추가")
    @WithAccount("soyo")
    @Test
    void test_addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT+SETTINGS+TAGS+"/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");

        Account soyo = accountRepository.findByNickname("soyo");

        assertThat(newTag).isNotNull();
        assertTrue(soyo.getTags().contains(newTag));
    }

    @DisplayName("계정 내 태그 삭제")
    @WithAccount("soyo")
    @Test
    void test_removeTag() throws Exception {
        Account soyo = accountRepository.findByNickname("soyo");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(soyo, newTag);

        assertTrue(soyo.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT+SETTINGS+TAGS+"/remove")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(tagForm))
            .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(soyo.getTags().contains(newTag));
    }

    @DisplayName("계정 지역 정보 추가")
    @WithAccount("soyo")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT+SETTINGS + ZONES + "/add")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(zoneForm))
        .with(csrf()))
                .andExpect(status().isOk());

        Account soyo = accountRepository.findByNickname("soyo");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(soyo.getZones().contains(zone));
    }

    @DisplayName("계정 지역 정보 삭제")
    @WithAccount("soyo")
    @Test
    void removeZone() throws Exception {
        Account soyo = accountRepository.findByNickname("soyo");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(soyo, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT+SETTINGS + ZONES + "/remove")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(zoneForm))
        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(soyo.getZones().contains(zone));
    }

}