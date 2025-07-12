package prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prosilion.superconductor.lib.jpa.dto.standard.EventTagDto;
import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityEventTagEntity;
import prosilion.superconductor.lib.jpa.entity.standard.EventTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityEventTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.standard.EventTagEntityRepository;

@Component
public class EventTagPlugin<
    P extends EventTag,
    Q extends EventTagEntityRepository<R>,
    R extends EventTagEntity,
    S extends EventEntityEventTagEntity,
    T extends EventEntityEventTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public EventTagPlugin(@NonNull EventTagEntityRepository<R> repo, @NonNull EventEntityEventTagEntityRepository<S> join) {
    super(repo, join, "e");
  }

  @Override
  public EventTagDto getTagDto(@NonNull P eventTag) {
    return new EventTagDto(eventTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long eventTagId) {
    return (S) new EventEntityEventTagEntity(eventId, eventTagId);
  }
}
