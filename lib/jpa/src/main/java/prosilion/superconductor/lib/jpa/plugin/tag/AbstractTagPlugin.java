package prosilion.superconductor.lib.jpa.plugin.tag;

import lombok.Getter;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.BaseTag;
import prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;

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
