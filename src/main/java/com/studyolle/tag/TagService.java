package com.studyolle.tag;

import com.studyolle.domain.Tag;
import com.studyolle.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle)
                .orElse(tagRepository.save(Tag.builder().title(tagTitle).build()));
        return tag;
    }
}
