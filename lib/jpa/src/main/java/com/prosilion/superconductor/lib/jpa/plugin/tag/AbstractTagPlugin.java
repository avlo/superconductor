package com.prosilion.superconductor.lib.jpa.plugin.tag;

import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;

@Getter
public abstract class AbstractTagPlugin<
    P extends BaseTag,
    Q extends AbstractTagJpaEntityRepository<R>,
    R extends AbstractTagJpaEntity,
    S extends EventEntityAbstractJpaEntity,
    T extends EventEntityAbstractTagJpaEntityRepository<S>> implements TagPlugin<P, Q, R, S, T> {

  private final AbstractTagJpaEntityRepository<R> repo;
  private final EventEntityAbstractTagJpaEntityRepository<S> join;
  private final String code;

  public AbstractTagPlugin(
      @NonNull AbstractTagJpaEntityRepository<R> repo,
      @NonNull EventEntityAbstractTagJpaEntityRepository<S> join,
      @NonNull String code) {
    this.repo = repo;
    this.join = join;
    this.code = code;
  }
}
