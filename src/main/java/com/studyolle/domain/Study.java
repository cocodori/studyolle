package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of="id")
@Entity
public class Study {
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers;

    @ManyToMany
    private Set<Account> members;

    @Column(unique = true)
    private String path;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags;

    @ManyToMany
    private Set<Zone> zones;

    private LocalDateTime publishedDateTime; //스터디 공개

    private LocalDateTime closedDateTime;   // 스터디 모집 종료

    private LocalDateTime recruiingUpdatedDateTime; //모집 제한

    private boolean recruiting; //모집 여부

    private boolean published; // 공개 여부

    private boolean closed; //종료 여부

    private boolean useBanner; //배너 사용 여부
}
