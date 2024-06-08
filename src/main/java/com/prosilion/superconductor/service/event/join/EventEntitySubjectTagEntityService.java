package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.SubjectTagDto;
import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.repository.SubjectTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepository;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class EventEntitySubjectTagEntityService {
  private final SubjectTagEntityRepository subjectTagEntityRepository;
  private final EventEntitySubjectTagEntityRepository join;

  @Autowired
  public EventEntitySubjectTagEntityService(SubjectTagEntityRepository subjectTagEntityRepository, EventEntitySubjectTagEntityRepository join) {
    this.subjectTagEntityRepository = subjectTagEntityRepository;
    this.join = join;
  }

  public Optional<SubjectTagEntity> getTags(Long eventId) {
    // TODO: refactor using stream proper
    Optional<EventEntitySubjectTagEntity> firstById = join.findFirstById(eventId);
    if (firstById.isPresent()) {
      return subjectTagEntityRepository.findFirstById(firstById.get().getId());
    }
    return Optional.empty();
  }

  public void saveSubjectTag(GenericEvent event, Long id) {
    List<BaseTag> baseTags = getRelevantTags(event);
    baseTags.stream().filter(baseTag -> "subject".equals(baseTag.getCode()))
        .findFirst()
        .ifPresent(subjectTag -> saveSubjectTag(new SubjectTagDto(((SubjectTag) subjectTag).getSubject()), id));
  }

  public List<BaseTag> getRelevantTags(GenericEvent event) {
    return Optional.of(event.getTags().stream()).orElse(Stream.empty())
        .filter(baseTag -> "subject".equals(baseTag.getCode()))
        .toList();
  }

  private void saveSubjectTag(SubjectTagDto subjectTag, Long id) {
    saveJoin(id, saveTag(subjectTag));
  }

  private Long saveTag(SubjectTagDto tag) {
    return subjectTagEntityRepository.save(tag.convertDtoToEntity()).getId();
  }

  private void saveJoin(Long eventId, Long tagId) {
    join.save(new EventEntitySubjectTagEntity(eventId, tagId));
  }
}