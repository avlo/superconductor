package com.prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.GeohashTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.prosilion.superconductor.lib.jpa.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.standard.GeohashTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityGeohashTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.standard.GeohashTagEntityRepository;

@Component
public class GeohashTagPlugin<
    P extends GeohashTag,
    Q extends GeohashTagEntityRepository<R>,
    R extends GeohashTagEntity,
    S extends EventEntityGeohashTagEntity,
    T extends EventEntityGeohashTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public GeohashTagPlugin(@Nonnull GeohashTagEntityRepository<R> repo, @NonNull EventEntityGeohashTagEntityRepository<S> join) {
    super(repo, join, "g");
  }

  @Override
  public GeohashTagDto getTagDto(@NonNull P geohashTag) {
    return new GeohashTagDto(geohashTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntityGeohashTagEntity(eventId, subjectTagId);
  }
}
