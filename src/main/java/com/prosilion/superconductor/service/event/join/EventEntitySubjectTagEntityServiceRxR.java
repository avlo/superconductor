package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.SubjectTagDtoRxR;
import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntityRxR;
import com.prosilion.superconductor.repository.SubjectTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepositoryRxR;
import com.prosilion.superconductor.service.event.join.standard.EventEntityStandardTagEntityServiceIFRxR;
import jakarta.transaction.Transactional;
import lombok.Getter;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Service
@Transactional
public class EventEntitySubjectTagEntityServiceRxR<T extends SubjectTagEntityRxR, U extends EventEntitySubjectTagEntityRxR> implements EventEntityStandardTagEntityServiceIFRxR<T, U> {
  private final SubjectTagEntityRepositoryRxR<T> subjectTagEntityRepository;
  private final EventEntitySubjectTagEntityRepositoryRxR<U> join;
  private final Class<T> clazz;

  @Autowired
  public EventEntitySubjectTagEntityServiceRxR(SubjectTagEntityRepositoryRxR<T> subjectTagEntityRepository, EventEntitySubjectTagEntityRepositoryRxR<U> join) {
    this.subjectTagEntityRepository = subjectTagEntityRepository;
    this.join = join;
    this.clazz = (Class<T>) SubjectTagEntityRxR.class;
  }

  public List<T> getTags(Long eventId) {
    List<U> eventEntitySubjectTagEntities = join.getAllByEventId(eventId);
    return eventEntitySubjectTagEntities.parallelStream().map(joinId -> subjectTagEntityRepository.findFirstById(joinId.getSubjectTagId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(clazz::cast)
        .toList();
  }

  public void saveTags(GenericEvent event, Long id) {
    List<BaseTag> baseTags = getRelevantTags(event);
    baseTags.stream().filter(baseTag -> "subject".equals(baseTag.getCode()))
        .findFirst()
        .ifPresent(subjectTag -> saveSubjectTag(new SubjectTagDtoRxR(((SubjectTag) subjectTag).getSubject()), id));
  }

  private List<BaseTag> getRelevantTags(GenericEvent event) {
    return Optional.of(event.getTags().stream()).orElse(Stream.empty())
        .filter(baseTag -> "subject".equals(baseTag.getCode()))
        .toList();
  }

  private void saveSubjectTag(SubjectTagDtoRxR subjectTag, Long id) {
    saveJoin(id, saveTag(subjectTag));
  }

  private Long saveTag(SubjectTagDtoRxR tag) {
    return subjectTagEntityRepository.save((T) tag.convertDtoToEntity()).getId();
  }

  private void saveJoin(Long eventId, Long tagId) {
    join.save((U) new EventEntitySubjectTagEntityRxR(eventId, tagId));
  }
}