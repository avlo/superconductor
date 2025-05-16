package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.standard.HashtagTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityHashtagTagEntity;
import com.prosilion.superconductor.entity.standard.HashtagTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityHashtagTagEntityRepository;
import com.prosilion.superconductor.repository.standard.HashtagTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import nostr.event.tag.HashtagTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HashtagTagPlugin<
    P extends HashtagTag,
    Q extends HashtagTagEntityRepository<R>,
    R extends HashtagTagEntity,
    S extends EventEntityHashtagTagEntity,
    T extends EventEntityHashtagTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public HashtagTagPlugin(@Nonnull HashtagTagEntityRepository<R> repo, @NonNull EventEntityHashtagTagEntityRepository<S> join) {
    super(repo, join, "t");
  }

  @Override
  public HashtagTagDto getTagDto(@NonNull P hashtagTag) {
    return new HashtagTagDto(hashtagTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntityHashtagTagEntity(eventId, subjectTagId);
  }
}
