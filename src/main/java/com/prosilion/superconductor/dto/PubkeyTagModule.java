package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntityRxR;
import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityPubkeyTagEntityRepository;
import com.prosilion.superconductor.repository.standard.PubkeyTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class PubkeyTagModule<
    P extends PubKeyTag,
    Q extends PubkeyTagEntityRepository<R>,
    R extends PubkeyTagEntity,
    S extends EventEntityPubkeyTagEntityRxR,
    U extends EventEntityPubkeyTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  private final PubkeyTagEntityRepository<R> pubkeyTagEntityRepository;
  private final EventEntityPubkeyTagEntityRepository<S> join;

  @Autowired
  public PubkeyTagModule(@Nonnull PubkeyTagEntityRepository<R> pubkeyTagEntityRepository, @NonNull EventEntityPubkeyTagEntityRepository<S> join) {
    this.pubkeyTagEntityRepository = pubkeyTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "p";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) PubkeyTagEntity.class;
  }

  @Override
  public R convertDtoToEntity(P pubkeyTag) {
    return (R) getTagDto(pubkeyTag).convertDtoToEntity();
  }

  @Override
  public PubkeyTagDto getTagDto(P pubkeyTag) {
    return new PubkeyTagDto(pubkeyTag);
  }

  @Override
  public S getEventEntityTagEntity(Long eventId, Long subjectTagId) {
    return (S) new EventEntityPubkeyTagEntityRxR(eventId, subjectTagId);
  }

  @Override
  public U getEventEntityStandardTagEntityRepositoryJoin() {
    return (U) join;
  }

  @Override
  public Q getStandardTagEntityRepositoryRxR() {
    return (Q) pubkeyTagEntityRepository;
  }
}
