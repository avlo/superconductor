package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntityRxR;
import com.prosilion.superconductor.repository.SubjectTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepositoryRxR;
import com.prosilion.superconductor.service.event.join.EventEntitySubjectTagEntityServiceRxR;
import nostr.event.BaseTag;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubjectTagModule<
    P extends SubjectTag,
    Q extends SubjectTagEntityRepositoryRxR<R>,
    R extends SubjectTagEntityRxR,
    S extends EventEntitySubjectTagEntityRxR,
    T extends EventEntitySubjectTagEntityServiceRxR<R, S>,
    U extends EventEntitySubjectTagEntityRepositoryRxR<S>>

    implements TagModule<P, Q, R, S, T, U> {

  private final EventEntitySubjectTagEntityServiceRxR<R, S> entityService;
  private P subjectTag;

  @Autowired
  public SubjectTagModule(EventEntitySubjectTagEntityServiceRxR<R, S> entityService) {
    this.entityService = entityService;
  }

  @Override
  public String getCode() {
    return "subject";
  }

  @Override
  public void setBaseTag(P subjectTag) {
    this.subjectTag = subjectTag;
  }

  @Override
  public BaseTag getBaseTag() {
    return subjectTag;
  }

  @Override
  public R convertDtoToEntity() {
    return getTagDto().convertDtoToEntity();
  }

  @Override
  public StandardTagDtoRxR getTagDto() {
    return new SubjectTagDtoRxR(subjectTag.getSubject());
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long subjectTagId) {
    return (S) new EventEntitySubjectTagEntityRxR(eventId, subjectTagId);
  }

  @Override
  public U getEventEntityStandardTagEntityRepositoryJoin() {
    return (U) entityService.getJoin();
  }

  @Override
  public T getEventEntityStandardTagEntityServiceRxR() {
    return (T) entityService;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) entityService.getSubjectTagEntityRepository();
  }
}
