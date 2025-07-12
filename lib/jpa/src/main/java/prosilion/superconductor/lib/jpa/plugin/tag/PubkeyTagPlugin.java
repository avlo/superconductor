package prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prosilion.superconductor.lib.jpa.dto.standard.PubkeyTagDto;
import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityPubkeyTagEntity;
import prosilion.superconductor.lib.jpa.entity.standard.PubkeyTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityPubkeyTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.standard.PubkeyTagEntityRepository;

@Component
public class PubkeyTagPlugin<
    P extends PubKeyTag,
    Q extends PubkeyTagEntityRepository<R>,
    R extends PubkeyTagEntity,
    S extends EventEntityPubkeyTagEntity,
    T extends EventEntityPubkeyTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public PubkeyTagPlugin(@Nonnull PubkeyTagEntityRepository<R> repo, @NonNull EventEntityPubkeyTagEntityRepository<S> join) {
    super(repo, join, "p");
  }

  @Override
  public PubkeyTagDto getTagDto(@NonNull P pubkeyTag) {
    return new PubkeyTagDto(pubkeyTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long pubkeyId) {
    return (S) new EventEntityPubkeyTagEntity(eventId, pubkeyId);
  }
}
