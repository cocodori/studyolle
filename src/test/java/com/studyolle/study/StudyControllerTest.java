package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.domain.Study;
import com.studyolle.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @WithAccount("soyo")
    @DisplayName("스터디 개설 테스트")
    @Test
    void test_StudySubmit() throws Exception {
        String path = "test-test";

        mockMvc.perform(post("/new-study")
                .param("path", path)
                .param("title", "title")
                .param("shortDescription", "short")
                .param("fullDescription", "full")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+path));

        Study getStudy = studyRepository.findByPath(path);

        assertEquals(getStudy.getPath(), path);
    }



}
