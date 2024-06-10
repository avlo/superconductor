package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.PubkeyTagDto;
import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntityRxR;
import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityPubkeyTagEntityRepository;
import com.prosilion.superconductor.repository.standard.PubkeyTagEntityRepository;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PubkeyTagModule<
    P extends PubKeyTag,
    Q extends PubkeyTagEntityRepository<R>,
    R extends PubkeyTagEntity,
    S extends EventEntityPubkeyTagEntityRxR,
    U extends EventEntityPubkeyTagEntityRepository<S>>
    implements TagModule<P, Q, R, S, U> {

  PubkeyTagEntityRepository<R> pubkeyTagEntityRepository;
  EventEntityPubkeyTagEntityRepository<S> join;
  private P pubkeyTag;

  @Autowired
  public PubkeyTagModule(PubkeyTagEntityRepository<R> pubkeyTagEntityRepository, EventEntityPubkeyTagEntityRepository<S> join) {
    this.pubkeyTagEntityRepository = pubkeyTagEntityRepository;
    this.join = join;
  }

  @Override
  public String getCode() {
    return "pubkey";
  }

  @Override
  public Class<R> getClazz() {
    return (Class<R>) PubkeyTagEntity.class;
  }

  @Override
  public void setBaseTag(P pubkeyTag) {
    this.pubkeyTag = pubkeyTag;
  }

  @Override
  public R convertDtoToEntity() {
    return getTagDto().convertDtoToEntity();
  }

  @Override
  public StandardTagDtoRxR getTagDto() {
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
