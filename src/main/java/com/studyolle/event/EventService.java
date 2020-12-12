package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreateBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);

        return eventRepository.save(event);
    }

    //TODO 모집 인원을 늘린 선착순 모임의 경우 자동으로 추가 인원 참가 신청을 확정 상태로 변경해야 함
    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
