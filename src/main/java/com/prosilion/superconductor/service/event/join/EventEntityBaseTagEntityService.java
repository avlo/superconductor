package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.BaseTagDto;
import com.prosilion.superconductor.entity.join.EventEntityBaseTagEntity;
import com.prosilion.superconductor.repository.BaseTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityBaseTagEntityRepository;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    saveJoins(id, saveTags(baseTagsOnly.stream().map(this::getValue).toList()));
  }

  private BaseTagValueMapper getValue(BaseTag baseTag) {
    return baseTag.getCode().equals("e") ?
        new BaseTagValueMapper(baseTag, ((EventTag) baseTag).getIdEvent()) :
        new BaseTagValueMapper(baseTag, ((PubKeyTag) baseTag).getPublicKey().toString());
  }

  private List<Long> saveTags(List<BaseTagValueMapper> tags) {
    return tags.stream().map(tagValueMapper ->
        baseTagEntityRepository.save(map(tagValueMapper).convertDtoToEntity()).getId()).toList();
  }

  private BaseTagDto map(BaseTagValueMapper tagValueMapper) {
    BaseTagDto dto = new BaseTagDto(tagValueMapper.value());
    dto.setKey(tagValueMapper.baseTag().getCode());
    return dto;
  }

  private void saveJoins(Long eventId, List<Long> tagIds) {
    tagIds.stream().map(tagId -> new EventEntityBaseTagEntity(eventId, tagId))
        .forEach(join::save);
  }

  public record BaseTagValueMapper(BaseTag baseTag, String value) {
  }
}
