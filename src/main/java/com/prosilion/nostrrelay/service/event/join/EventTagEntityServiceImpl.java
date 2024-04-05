package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.tag.BaseTagDto;
import com.prosilion.nostrrelay.entity.BaseTagEntity;
import com.prosilion.nostrrelay.entity.join.EventTagEntityJoin;
import com.prosilion.nostrrelay.repository.BaseTagRepository;
import com.prosilion.nostrrelay.repository.join.EventTagEntityRepositoryJoin;
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
  private final BaseTagRepository baseTagRepository;
  private final EventTagEntityRepositoryJoin eventTagEntityRepositoryJoin;

  public EventTagEntityServiceImpl() {
    baseTagRepository = ApplicationContextProvider.getApplicationContext().getBean(BaseTagRepository.class);
    eventTagEntityRepositoryJoin = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityRepositoryJoin.class);
  }

  @Transactional
  public Long save(GenericEvent genericEvent, Long id) throws InvocationTargetException, IllegalAccessException {
    List<Long> savedTagIds = saveTags(genericEvent);
    saveEventTags(id, savedTagIds);
    return id;
  }

  private List<Long> saveTags(GenericEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    List<Long> savedIds = new ArrayList<>();
    for (BaseTag baseTag : event.getTags()) {
      BaseTagDto dto = new BaseTagDto(((ValueTag) baseTag).getValue());
      dto.setKey(baseTag.getCode());
      BaseTagEntity entity = dto.convertDtoToEntity();
      savedIds.add(Optional.of(baseTagRepository.save(entity)).orElseThrow(NoResultException::new).getId());
    }
    return savedIds;
  }

  private void saveEventTags(Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      Optional.of(eventTagEntityRepositoryJoin.save(new EventTagEntityJoin(eventId, tagId))).orElseThrow(NoResultException::new);
    }
  }
}
