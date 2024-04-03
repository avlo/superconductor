package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.tag.BaseTagDto;
import com.prosilion.nostrrelay.dto.event.TextNoteEventDto;
import com.prosilion.nostrrelay.entity.BaseTagEntity;
import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import com.prosilion.nostrrelay.entity.join.EventTagEntity;
import com.prosilion.nostrrelay.repository.BaseTagRepository;
import com.prosilion.nostrrelay.repository.EventRepository;
import com.prosilion.nostrrelay.repository.join.EventTagEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.ValueTag;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventTagEntityServiceImpl {
  private final EventRepository eventRepository;
  private final BaseTagRepository baseTagRepository;
  private final EventTagEntityRepository eventTagEntityRepository;

  public EventTagEntityServiceImpl() {
    eventRepository = ApplicationContextProvider.getApplicationContext().getBean(EventRepository.class);
    baseTagRepository = ApplicationContextProvider.getApplicationContext().getBean(BaseTagRepository.class);
    eventTagEntityRepository = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityRepository.class);
  }

  @Transactional
  public void save(GenericEvent textNoteEvent, String id) throws InvocationTargetException, IllegalAccessException {
    Long savedEventId = saveEntity(textNoteEvent, id);
    List<Long> savedTagIds = saveTags(textNoteEvent);
    saveEventTags(savedEventId, savedTagIds);
  }

  private Long saveEntity(GenericEvent event, String id) throws InvocationTargetException, IllegalAccessException, NoResultException {
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEventDto.setId(id);
    textNoteEventDto.setCreatedAt(event.getCreatedAt());
    textNoteEventDto.setSignature(event.getSignature());
    TextNoteEventEntity textNoteEventEntity = Optional.of(eventRepository.save(textNoteEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
    return textNoteEventEntity.getId();
  }

  private List<Long> saveTags(GenericEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    List<Long> savedIds = new ArrayList<>();
    for (BaseTag baseTag : event.getTags()) {
      BaseTagDto dto = new BaseTagDto(((ValueTag)baseTag).getValue());
      dto.setKey(baseTag.getCode());
      BaseTagEntity entity = dto.convertDtoToEntity();
      savedIds.add(Optional.of(baseTagRepository.save(entity)).orElseThrow(NoResultException::new).getId());
    }
    return savedIds;
  }

  private void saveEventTags(Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      Optional.of(eventTagEntityRepository.save(new EventTagEntity(eventId, tagId))).orElseThrow(NoResultException::new);
    }
  }
}
