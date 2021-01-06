package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {
    boolean existsByPath(String path);

    Study findStudyWithMembersByPath(String path);

    Study findStudyOnlyByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(attributePaths = {"managers"})
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = {"tags","zones"})
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithManagersAndMembersById(Long id);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
