package com.studyolle.modules.study;

import com.studyolle.infra.MockMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@MockMvcTest
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @DisplayName("findByPath() TEST")
    @Test
    void test_findByPath() {
        String path = "test-test";

        studyRepository.save(Study.builder()
                .path(path)
                .title("test")
                .build());

        Study findByPath = studyRepository.findByPath(path);

        assertEquals(path, findByPath.getPath());
    }

}