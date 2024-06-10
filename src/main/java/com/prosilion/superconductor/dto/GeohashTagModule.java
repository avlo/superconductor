package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.generic.GeohashTagDto;
import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.generic.GeohashTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGeohashTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.GeohashTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class GeohashTagModule<
    P extends GeohashTag,
    Q extends GeohashTagEntityRepository<R>,
    R extends GeohashTagEntity,
    S extends EventEntityGeohashTagEntity,
    U extends EventEntityGeohashTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  private final GeohashTagEntityRepository<R> geohashTagEntityRepository;
  private final EventEntityGeohashTagEntityRepository<S> join;

  @Autowired
  public GeohashTagModule(@Nonnull GeohashTagEntityRepository<R> geohashTagEntityRepository, @NonNull EventEntityGeohashTagEntityRepository<S> join) {
    this.geohashTagEntityRepository = geohashTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "g";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) GeohashTagEntity.class;
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
  public U getEventEntityStandardTagEntityRepositoryJoin() {
    return (U) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) geohashTagEntityRepository;
  }
}
