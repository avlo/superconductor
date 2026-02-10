package com.prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.HashtagTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.ConcreteTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityHashtagTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.HashtagTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityHashtagTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.HashtagTagJpaEntityRepository;

@Component
public class HashtagTagPlugin<
    P extends HashtagTag,
    Q extends HashtagTagJpaEntityRepository<R>,
    R extends HashtagTagJpaEntity,
    S extends EventEntityHashtagTagJpaEntity,
    T extends EventEntityHashtagTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public HashtagTagPlugin(@Nonnull HashtagTagJpaEntityRepository<R> repo, @NonNull EventEntityHashtagTagJpaEntityRepository<S> join) {
    super(repo, join, "t");
  }

  @Override
  public ConcreteTagDto getTagDto(@NonNull P hashtagTag) {
    return new ConcreteTagDto<>(hashtagTag, HashtagTagJpaEntity::new);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntityHashtagTagJpaEntity(eventId, subjectTagId);
  }
}
