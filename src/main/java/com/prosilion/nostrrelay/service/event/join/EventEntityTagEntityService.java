package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.util.BaseTagValueMapper;
import com.prosilion.nostrrelay.dto.BaseTagDto;
import com.prosilion.nostrrelay.entity.BaseTagEntity;
import com.prosilion.nostrrelay.entity.join.EventEntityTagEntity;
import com.prosilion.nostrrelay.repository.BaseTagEntityRepository;
import com.prosilion.nostrrelay.repository.join.EventEntityTagEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventEntityTagEntityService {
  private final BaseTagEntityRepository baseTagEntityRepository;
  private final EventEntityTagEntityRepository join;

  public EventEntityTagEntityService(BaseTagEntityRepository baseTagEntityRepository, EventEntityTagEntityRepository join) {
    this.baseTagEntityRepository = baseTagEntityRepository;
    this.join = join;
  }

  @Transactional
  public Long saveBaseTags(List<BaseTagValueMapper> tags, Long id) {
    List<Long> savedTagIds = saveTags(tags);
    saveEventTags(id, savedTagIds);
    return id;
  }

  private List<Long> saveTags(List<BaseTagValueMapper> tags) {
    List<Long> savedIds = new ArrayList<>();
    for (BaseTagValueMapper tagValueMapper : tags) {
      BaseTagDto dto = new BaseTagDto(tagValueMapper.value());
      dto.setKey(tagValueMapper.baseTag().getCode());
      BaseTagEntity entity = dto.convertDtoToEntity();
      savedIds.add(Optional.of(baseTagEntityRepository.save(entity)).orElseThrow(NoResultException::new).getId());
    }
    return savedIds;
  }

  private void saveEventTags(Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      Optional.of(join.save(new EventEntityTagEntity(eventId, tagId))).orElseThrow(NoResultException::new);
    }
  }
}
