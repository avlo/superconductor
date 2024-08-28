package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.entity.standard.GeohashTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityGeohashTagEntityRepository;
import com.prosilion.superconductor.repository.standard.GeohashTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import nostr.event.tag.GeohashTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeohashTagPlugin<
    P extends GeohashTag,
    Q extends GeohashTagEntityRepository<R>,
    R extends GeohashTagEntity,
    S extends EventEntityGeohashTagEntity,
    T extends EventEntityGeohashTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, T> {

  private final GeohashTagEntityRepository<R> geohashTagEntityRepository;
  private final EventEntityGeohashTagEntityRepository<S> join;

  @Autowired
  public GeohashTagPlugin(@Nonnull GeohashTagEntityRepository<R> geohashTagEntityRepository, @NonNull EventEntityGeohashTagEntityRepository<S> join) {
    this.geohashTagEntityRepository = geohashTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "g";
  }

  @Override
  public R convertDtoToEntity(P geohashTag) {
    return (R) getTagDto(geohashTag).convertDtoToEntity();
  }

  @Override
  public GeohashTagDto getTagDto(P geohashTag) {
    return new GeohashTagDto(geohashTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long subjectTagId) {
    return (S) new EventEntityGeohashTagEntity(eventId, subjectTagId);
  }

  @Override
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepository() {
    return (Q) geohashTagEntityRepository;
  }
}
