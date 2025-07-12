package prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.AddressTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import prosilion.superconductor.lib.jpa.dto.standard.AddressTagDto;
import prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityAddressTagEntity;
import prosilion.superconductor.lib.jpa.entity.standard.AddressTagEntity;
import prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityAddressTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.standard.AddressTagEntityRepository;

@Component
public class AddressTagPlugin<
    P extends AddressTag,
    Q extends AddressTagEntityRepository<R>,
    R extends AddressTagEntity,
    S extends EventEntityAddressTagEntity,
    T extends EventEntityAddressTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {

  @Autowired
  public AddressTagPlugin(@NonNull AddressTagEntityRepository<R> repo, @NonNull EventEntityAddressTagEntityRepository<S> join) {
    super(repo, join, "a");
  }

  @Override
  public AddressTagDto getTagDto(@NonNull P addressTag) {
    return new AddressTagDto(addressTag);
  }

  @Override
  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long addressTagId) {
    return (S) new EventEntityAddressTagEntity(eventId, addressTagId);
  }
}
