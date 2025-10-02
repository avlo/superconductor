package com.prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.GeohashTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityGeohashTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.GeohashTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityGeohashTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.GeohashTagJpaEntityRepository;

@Component
public class GeohashTagPlugin<
    P extends GeohashTag,
    Q extends GeohashTagJpaEntityRepository<R>,
    R extends GeohashTagJpaEntity,
    S extends EventEntityGeohashTagJpaEntity,
    T extends EventEntityGeohashTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public GeohashTagPlugin(@Nonnull GeohashTagJpaEntityRepository<R> repo, @NonNull EventEntityGeohashTagJpaEntityRepository<S> join) {
    super(repo, join, "g");
  }

  @Override
  public GeohashTagDto getTagDto(@NonNull P geohashTag) {
    return new GeohashTagDto(geohashTag);
  }

  @Override
  public S getEventEntityTagJpaEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntityGeohashTagJpaEntity(eventId, subjectTagId);
  }
}
