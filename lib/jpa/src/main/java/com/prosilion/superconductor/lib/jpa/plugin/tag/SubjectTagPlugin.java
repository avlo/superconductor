package com.prosilion.superconductor.lib.jpa.plugin.tag;

import com.prosilion.nostr.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
  public SubjectTagPlugin(SubjectTagJpaEntityRepository<R> repo, EventEntitySubjectTagJpaEntityRepository<S> join) {
    super(repo, join, "subject",
        tag -> (R) new SubjectTagJpaEntity(tag.getSubject().chars().limit(80)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()),
        (eid, tid) -> (S) new EventEntitySubjectTagJpaEntity(eid, tid));
  }
}
