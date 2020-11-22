package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private LocalDateTime recruiingUpdatedDateTime; //모집 제한

    private boolean recruiting; //모집 여부

    private boolean published; // 공개 여부

    private boolean closed; //종료 여부

    private boolean useBanner; //배너 사용 여부

    public void addManager(Account account) {
        this.managers.add(account);
    }
}
