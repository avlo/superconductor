package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.entity.standard.SubjectTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntitySubjectTagEntityRepository;
import com.prosilion.superconductor.repository.standard.SubjectTagEntityRepository;
import lombok.Getter;
import lombok.NonNull;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SubjectTagPlugin<
    P extends SubjectTag,
    Q extends SubjectTagEntityRepository<R>,
    R extends SubjectTagEntity,
    S extends EventEntitySubjectTagEntity,
    T extends EventEntitySubjectTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, T> {

  private final SubjectTagEntityRepository<R> subjectTagEntityRepository;
  private final EventEntitySubjectTagEntityRepository<S> join;

  @Autowired
  public SubjectTagPlugin(@NonNull SubjectTagEntityRepository<R> subjectTagEntityRepository, @NonNull EventEntitySubjectTagEntityRepository<S> join) {
    this.subjectTagEntityRepository = subjectTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "subject";
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
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) subjectTagEntityRepository;
  }
}
