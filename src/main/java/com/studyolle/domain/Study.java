package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")})
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of="id")
@Entity
public class Study {
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime; //스터디 공개

    private LocalDateTime closedDateTime;   // 스터디 모집 종료

    private LocalDateTime recruingUpdatedDateTime; //모집 제한

    private boolean recruiting; //모집 여부

    private boolean published; // 공개 여부

    private boolean closed; //종료 여부

    private boolean useBanner; //배너 사용 여부

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();

        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public void publish() { //스터디 공개
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
            return;
        }

        throw new RuntimeException("스터디를 공개할 수 없는 상태입니다.");
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
            return;
        }

        throw new RuntimeException("스터디를 종료할 수 없습니다.");
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruingUpdatedDateTime = LocalDateTime.now();
            return;
        }
        throw new RuntimeException("인원 모집을 시작할 수 없습니다.");
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruingUpdatedDateTime = LocalDateTime.now();
            return;
        }
        throw new RuntimeException("모집을 멈출 수 없습니다.");
    }

    public boolean canUpdateRecruiting() {
        return this.published && this.recruingUpdatedDateTime == null
                || this.recruingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }
}
