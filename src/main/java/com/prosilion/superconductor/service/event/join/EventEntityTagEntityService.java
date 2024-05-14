package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.BaseTagDto;
import com.prosilion.superconductor.entity.BaseTagEntity;
import com.prosilion.superconductor.entity.join.EventEntityTagEntity;
import com.prosilion.superconductor.repository.BaseTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityTagEntityRepository;
import com.prosilion.superconductor.util.BaseTagValueMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventEntityTagEntityService {
  private final BaseTagEntityRepository baseTagEntityRepository;
  private final EventEntityTagEntityRepository join;

  public EventEntityTagEntityService(BaseTagEntityRepository baseTagEntityRepository, EventEntityTagEntityRepository join) {
    this.baseTagEntityRepository = baseTagEntityRepository;
    this.join = join;
  }

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
