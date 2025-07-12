package prosilion.superconductor.lib.jpa.plugin.tag;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.HashtagTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prosilion.superconductor.lib.jpa.dto.standard.HashtagTagDto;
import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityHashtagTagEntity;
import prosilion.superconductor.lib.jpa.entity.standard.HashtagTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityHashtagTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.standard.HashtagTagEntityRepository;

@Component
public class HashtagTagPlugin<
    P extends HashtagTag,
    Q extends HashtagTagEntityRepository<R>,
    R extends HashtagTagEntity,
    S extends EventEntityHashtagTagEntity,
    T extends EventEntityHashtagTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public HashtagTagPlugin(@Nonnull HashtagTagEntityRepository<R> repo, @NonNull EventEntityHashtagTagEntityRepository<S> join) {
    super(repo, join, "t");
  }

  @Override
  public HashtagTagDto getTagDto(@NonNull P hashtagTag) {
    return new HashtagTagDto(hashtagTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long subjectTagId) {
    return (S) new EventEntityHashtagTagEntity(eventId, subjectTagId);
  }
}
