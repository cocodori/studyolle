package com.studyolle.modules.zone;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ZoneService {
    private final ZoneRepository zoneRepository;

    @PostConstruct  //이 빈이 만들어질 때 실행되는 초기화 블럭
    public void initZoneData() throws IOException {
        //ZONE테이블에 테이터가 하나도 없다면 실행
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            //zones_kr.csv 데이터를 전부 객체로 읽어온다.
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8) //파일 패스와 캐릭터 셋을 인자로 받음
                    .stream()
                    .map(line -> {
                        //csv 데이터를 `,`단위로 잘라서 배열에 저장.
                        String[] split = line.split(",");
                        return Zone.builder()
                                .city(split[0])
                                .localNameOfCity(split[1])
                                .province(split[2])
                                .build();
                    }).collect(Collectors.toList());

            zoneRepository.saveAll(zoneList);
        }
    }

}
