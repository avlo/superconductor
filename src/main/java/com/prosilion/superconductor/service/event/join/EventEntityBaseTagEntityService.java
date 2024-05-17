package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.BaseTagDto;
import com.prosilion.superconductor.entity.BaseTagEntity;
import com.prosilion.superconductor.entity.join.EventEntityBaseTagEntity;
import com.prosilion.superconductor.repository.BaseTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityBaseTagEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventEntityBaseTagEntityService {
  private final BaseTagEntityRepository baseTagEntityRepository;
  private final EventEntityBaseTagEntityRepository join;

  @Autowired
  public EventEntityBaseTagEntityService(BaseTagEntityRepository baseTagEntityRepository, EventEntityBaseTagEntityRepository join) {
    this.baseTagEntityRepository = baseTagEntityRepository;
    this.join = join;
  }

  public void saveBaseTags(List<BaseTag> baseTagsOnly, Long id) {
    List<BaseTagValueMapper> tags = baseTagsOnly.stream().map(this::getValue).toList();
    List<Long> savedTagIds = saveTags(tags);
    saveJoins(id, savedTagIds);
  }

  private BaseTagValueMapper getValue(BaseTag baseTag) {
    if (baseTag.getCode().equals("e")) // event
      return new BaseTagValueMapper(baseTag, ((EventTag) baseTag).getIdEvent());
    return new BaseTagValueMapper(baseTag, ((PubKeyTag) baseTag).getPublicKey().toString());
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

  private void saveJoins(Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      Optional.of(join.save(new EventEntityBaseTagEntity(eventId, tagId))).orElseThrow(NoResultException::new);
    }
  }
  public record BaseTagValueMapper(BaseTag baseTag, String value) {
  }
}
