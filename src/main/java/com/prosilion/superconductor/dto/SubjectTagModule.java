package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.repository.SubjectTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepository;
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
    Q extends SubjectTagEntityRepository<R>,
    R extends SubjectTagEntity,
    S extends EventEntitySubjectTagEntity,
    U extends EventEntitySubjectTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  private final SubjectTagEntityRepository<R> subjectTagEntityRepository;
  private final EventEntitySubjectTagEntityRepository<S> join;

  @Autowired
  public SubjectTagModule(@NonNull SubjectTagEntityRepository<R> subjectTagEntityRepository, @NonNull EventEntitySubjectTagEntityRepository<S> join) {
    this.subjectTagEntityRepository = subjectTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "subject";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) SubjectTagEntity.class;
  }

  @Override
  public R convertDtoToEntity(P subjectTag) {
    return (R) getTagDto(subjectTag).convertDtoToEntity();
  }

  @Override
  public SubjectTagDto getTagDto(P subjectTag) {
    return new SubjectTagDto(subjectTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long subjectTagId) {
    return (S) new EventEntitySubjectTagEntity(eventId, subjectTagId);
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
