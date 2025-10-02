package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntitySubjectTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.SubjectTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntitySubjectTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.SubjectTagJpaEntityRepository;

@Component
public class SubjectTagPlugin<
    P extends SubjectTag,
    Q extends SubjectTagJpaEntityRepository<R>,
    R extends SubjectTagJpaEntity,
    S extends EventEntitySubjectTagJpaEntity,
    T extends EventEntitySubjectTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public SubjectTagPlugin(@NonNull SubjectTagJpaEntityRepository<R> repo, @NonNull EventEntitySubjectTagJpaEntityRepository<S> join) {
    super(repo, join, "subject");
  }

  @Override
  public SubjectTagDto getTagDto(@NonNull P subjectTag) {
    return new SubjectTagDto(subjectTag);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntitySubjectTagJpaEntity(eventId, subjectTagId);
  }
}
