package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntityRxR;
import com.prosilion.superconductor.repository.SubjectTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepositoryRxR;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class SubjectTagModule<
    P extends SubjectTag,
    Q extends SubjectTagEntityRepositoryRxR<R>,
    R extends SubjectTagEntityRxR,
    S extends EventEntitySubjectTagEntityRxR,
    U extends EventEntitySubjectTagEntityRepositoryRxR<S>>
    implements TagModule<P, Q, R, S, U> {

  private final SubjectTagEntityRepositoryRxR<R> subjectTagEntityRepository;
  private final EventEntitySubjectTagEntityRepositoryRxR<S> join;

  @Autowired
  public SubjectTagModule(@NonNull SubjectTagEntityRepositoryRxR<R> subjectTagEntityRepository, @NonNull EventEntitySubjectTagEntityRepositoryRxR<S> join) {
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
  public R convertDtoToEntity(P subjectTag) {
    return (R) getTagDto(subjectTag).convertDtoToEntity();
  }

  @Override
  public SubjectTagDtoRxR getTagDto(P subjectTag) {
    return new SubjectTagDtoRxR(subjectTag);
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
