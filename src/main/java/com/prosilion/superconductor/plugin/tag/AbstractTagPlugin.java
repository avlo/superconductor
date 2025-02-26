package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.entity.AbstractTagEntity;
import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import lombok.Getter;
import lombok.NonNull;
import nostr.event.BaseTag;

public abstract class AbstractTagPlugin<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>,
    R extends AbstractTagEntity,
    S extends EventEntityAbstractTagEntity,
    T extends EventEntityAbstractTagEntityRepository<S>> implements TagPlugin<P, Q, R, S, T> {

  protected final AbstractTagEntityRepository<R> abstractTagEntityRepository;
  protected final EventEntityAbstractTagEntityRepository<S> join;

  @Getter
  private final String code;

  public AbstractTagPlugin(
      @NonNull AbstractTagEntityRepository<R> abstractTagEntityRepository,
      @NonNull EventEntityAbstractTagEntityRepository<S> join,
      @NonNull String code) {
    this.abstractTagEntityRepository = abstractTagEntityRepository;
    this.join = join;
    this.code = code;
  }

  @Override
  public R convertDtoToEntity(P baseTag) {
    return (R) getTagDto(baseTag).convertDtoToEntity();
  }

  @Override
  public T getEventEntityStandardTagEntityRepositoryJoin() {
    return (T) join;
  }

  @Override
  public Q getStandardTagEntityRepository() {
    return (Q) abstractTagEntityRepository;
  }
}
