package com.studyolle.modules.account;

import com.querydsl.core.types.Predicate;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;

        log.info("QAccount.class    : {} ", QAccount.class);
        log.info("QAccount.account  : {} ", account);

        return account
                .zones
                .any()
                .in(zones)
                    .and(account
                        .tags
                        .any()
                        .in(tags));
    }
}

