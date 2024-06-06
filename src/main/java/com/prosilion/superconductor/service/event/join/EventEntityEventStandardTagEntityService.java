package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.EventTagDto;
import com.prosilion.superconductor.entity.EventStandardTagEntity;
import com.prosilion.superconductor.entity.join.EventEntityEventStandardTagEntity;
import com.prosilion.superconductor.repository.EventStandardTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityEventStandardTagEntityRepository;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class EventEntityEventStandardTagEntityService {
  private final EventStandardTagEntityRepository eventStandardTagEntityRepository;
  private final EventEntityEventStandardTagEntityRepository join;

  @Autowired
  public EventEntityEventStandardTagEntityService(EventStandardTagEntityRepository eventStandardTagEntityRepository, EventEntityEventStandardTagEntityRepository join) {
    this.eventStandardTagEntityRepository = eventStandardTagEntityRepository;
    this.join = join;
  }

  public List<EventStandardTagEntity> getTags(Long eventId) {
    EventEntityEventStandardTagEntity referenceById = join.getReferenceById(eventId);
//    List<Long> allByEventId = join.findAllByEventId(eventId);
    return eventStandardTagEntityRepository.findAllById(List.of(referenceById.getEventStandardTagId()));
  }

  public void saveEventStandardTags(GenericEvent event, Long id) {
    List<BaseTag> eventStandardTags = getRelevantTags(event);
    saveJoins(id,
        saveTags(eventStandardTags.stream().map(
            this::getValue
        ).toList()));
  }

  private EventStandardTagValueMapper getValue(BaseTag baseTag) {
    return switch (baseTag.getCode()) {
      case "e" -> new EventStandardTagValueMapper(baseTag, ((EventTag) baseTag).getIdEvent());
      case "p" -> new EventStandardTagValueMapper(baseTag, ((PubKeyTag) baseTag).getPublicKey().toString());
//      TODO: complete "a" tag
      //     ["a", "30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd", "wss://nostr.example.com"],
      // kind : pubkey : <optional: dtag> : <optional: recommended relay url>
//      case "a" -> new EventStandardTagValueMapper(baseTag, (baseTag).toString());
      default -> throw new IllegalArgumentException("Unknown tag code: " + baseTag.getCode());
    };
  }

  private List<Long> saveTags(List<EventStandardTagValueMapper> tags) {
    return tags.stream().map(tagValueMapper ->
        eventStandardTagEntityRepository.save(map(tagValueMapper).convertDtoToEntity()).getId()).toList();
  }

  public static EventTagDto map(EventStandardTagValueMapper tagValueMapper) {
    EventTagDto dto = new EventTagDto(tagValueMapper.value());
    dto.setKey(tagValueMapper.baseTag().getCode());
    return dto;
  }

  private void saveJoins(Long eventId, List<Long> tagIds) {
    tagIds.stream().map(tagId -> new EventEntityEventStandardTagEntity(eventId, tagId))
        .forEach(join::save);
  }

  public record EventStandardTagValueMapper(BaseTag baseTag, String value) {
  }

  public List<BaseTag> getRelevantTags(GenericEvent event) {
    return Optional.of(event.getTags().stream()).orElse(Stream.empty())
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
  }
}