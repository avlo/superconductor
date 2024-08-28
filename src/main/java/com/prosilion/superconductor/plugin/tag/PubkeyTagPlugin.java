package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntity;
import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityPubkeyTagEntityRepository;
import com.prosilion.superconductor.repository.standard.PubkeyTagEntityRepository;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PubkeyTagPlugin<
    P extends PubKeyTag,
    Q extends PubkeyTagEntityRepository<R>,
    R extends PubkeyTagEntity,
    S extends EventEntityPubkeyTagEntity,
    T extends EventEntityPubkeyTagEntityRepository<S>>
    implements TagPlugin<P, Q, R, S, T> {

  private final PubkeyTagEntityRepository<R> pubkeyTagEntityRepository;
  private final EventEntityPubkeyTagEntityRepository<S> join;

  @Autowired
  public PubkeyTagPlugin(@Nonnull PubkeyTagEntityRepository<R> pubkeyTagEntityRepository, @NonNull EventEntityPubkeyTagEntityRepository<S> join) {
    this.pubkeyTagEntityRepository = pubkeyTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "p";
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
    return (S) new EventEntityPubkeyTagEntity(eventId, subjectTagId);
  }

  @Override
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepository() {
    return (Q) pubkeyTagEntityRepository;
  }
}
