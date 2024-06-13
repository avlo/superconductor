package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.HashtagTagDto;
import com.prosilion.superconductor.entity.standard.HashtagTagEntity;
import com.prosilion.superconductor.entity.join.standard.EventEntityHashtagTagEntity;
import com.prosilion.superconductor.repository.standard.HashtagTagEntityRepository;
import com.prosilion.superconductor.repository.join.standard.EventEntityHashtagTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.HashtagTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class HashtagTagPlugin<
    P extends HashtagTag,
    Q extends HashtagTagEntityRepository<R>,
    R extends HashtagTagEntity,
    S extends EventEntityHashtagTagEntity,
    T extends EventEntityHashtagTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, T> {

  private final HashtagTagEntityRepository<R> hashtagTagEntityRepository;
  private final EventEntityHashtagTagEntityRepository<S> join;

  @Autowired
  public HashtagTagPlugin(@Nonnull HashtagTagEntityRepository<R> hashtagTagEntityRepository, @NonNull EventEntityHashtagTagEntityRepository<S> join) {
    this.hashtagTagEntityRepository = hashtagTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "t";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) HashtagTagEntity.class;
  }

  @Override
  public R convertDtoToEntity(P hashtagTag) {
    return (R) getTagDto(hashtagTag).convertDtoToEntity();
  }

  @Override
  public HashtagTagDto getTagDto(P hashtagTag) {
    return new HashtagTagDto(hashtagTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long subjectTagId) {
    return (S) new EventEntityHashtagTagEntity(eventId, subjectTagId);
  }

  @Override
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) hashtagTagEntityRepository;
  }
}
