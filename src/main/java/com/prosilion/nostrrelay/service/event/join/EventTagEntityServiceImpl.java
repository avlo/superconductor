package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.BaseTagDto;
import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import com.prosilion.nostrrelay.entity.BaseTagEntity;
import com.prosilion.nostrrelay.repository.BaseTagRepository;
import com.prosilion.nostrrelay.repository.EventRepository;
import com.prosilion.nostrrelay.repository.join.EventTagEntityRepository;
import jakarta.persistence.NoResultException;
import nostr.event.BaseTag;
import nostr.event.impl.TextNoteEvent;
import nostr.event.tag.EventTag;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
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

  public void save(TextNoteEvent textNoteEvent, String id) throws InvocationTargetException, IllegalAccessException {
    saveEntity(textNoteEvent, id);
    saveTags(textNoteEvent);
  }

  private void saveEntity(TextNoteEvent event, String id) throws InvocationTargetException, IllegalAccessException, NoResultException {
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEventDto.setId(id);
    textNoteEventDto.setCreatedAt(event.getCreatedAt());
    textNoteEventDto.setSignature(event.getSignature());
    Optional.of(eventRepository.save(textNoteEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }

  private void saveTags(TextNoteEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    for (BaseTag baseTag : event.getTags()) {
      BaseTagDto dto = new BaseTagDto(((EventTag) baseTag).getIdEvent());
      dto.setKey(baseTag.getCode());
      BaseTagEntity entity = dto.convertDtoToEntity();
      Optional.of(baseTagRepository.save(entity)).orElseThrow(NoResultException::new);
    }
  }

  private void saveEventTags() {

  }
}
