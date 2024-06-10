package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntityRxR;
import com.prosilion.superconductor.repository.SubjectTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepositoryRxR;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubjectTagModule<
    P extends SubjectTag,
    Q extends SubjectTagEntityRepositoryRxR<R>,
    R extends SubjectTagEntityRxR,
    S extends EventEntitySubjectTagEntityRxR,
    U extends EventEntitySubjectTagEntityRepositoryRxR<S>>

    implements TagModule<P, Q, R, S, U> {

  SubjectTagEntityRepositoryRxR<R> subjectTagEntityRepository;
  EventEntitySubjectTagEntityRepositoryRxR<S> join;
  private P subjectTag;

  @Autowired
  public SubjectTagModule(SubjectTagEntityRepositoryRxR<R> subjectTagEntityRepository, EventEntitySubjectTagEntityRepositoryRxR<S> join) {
    this.subjectTagEntityRepository = subjectTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "subject";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) SubjectTagEntityRxR.class;
  }

  @Override
  public void setBaseTag(P subjectTag) {
    this.subjectTag = subjectTag;
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
    return (U) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) subjectTagEntityRepository;
  }
}
