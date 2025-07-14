package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.SubjectTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.SubjectTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntitySubjectTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.SubjectTagEntityRepository;

@Component
public class SubjectTagPlugin<
    P extends SubjectTag,
    Q extends SubjectTagEntityRepository<R>,
    R extends SubjectTagEntity,
    S extends EventEntitySubjectTagEntity,
    T extends EventEntitySubjectTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public SubjectTagPlugin(@NonNull SubjectTagEntityRepository<R> repo, @NonNull EventEntitySubjectTagEntityRepository<S> join) {
    super(repo, join, "subject");
  }

  @Override
  public SubjectTagDto getTagDto(@NonNull P subjectTag) {
    return new SubjectTagDto(subjectTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntitySubjectTagEntity(eventId, subjectTagId);
  }
}
