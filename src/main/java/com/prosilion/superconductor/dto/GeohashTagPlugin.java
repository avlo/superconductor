package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.GeohashTagDto;
import com.prosilion.superconductor.entity.standard.GeohashTagEntity;
import com.prosilion.superconductor.entity.join.standard.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.standard.GeohashTagEntityRepository;
import com.prosilion.superconductor.repository.join.standard.EventEntityGeohashTagEntityRepository;
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
public class GeohashTagPlugin<
    P extends GeohashTag,
    Q extends GeohashTagEntityRepository<R>,
    R extends GeohashTagEntity,
    S extends EventEntityGeohashTagEntity,
    U extends EventEntityGeohashTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, U> {

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
