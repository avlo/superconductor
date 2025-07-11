package com.prosilion.superconductor.base.plugin.tag;

import com.prosilion.superconductor.base.entity.AbstractTagEntity;
import com.prosilion.superconductor.base.entity.join.EventEntityAbstractEntity;
import com.prosilion.superconductor.base.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.BaseTag;

@Getter
public abstract class AbstractTagPlugin<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>,
    R extends AbstractTagEntity,
    S extends EventEntityAbstractEntity,
    T extends EventEntityAbstractTagEntityRepository<S>> implements TagPlugin<P, Q, R, S, T> {

  private final AbstractTagEntityRepository<R> repo;
  private final EventEntityAbstractTagEntityRepository<S> join;
  private final String code;

  public AbstractTagPlugin(
      @NonNull AbstractTagEntityRepository<R> repo,
      @NonNull EventEntityAbstractTagEntityRepository<S> join,
      @NonNull String code) {
    this.repo = repo;
    this.join = join;
    this.code = code;
  }
}
