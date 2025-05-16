package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.standard.AddressTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityAddressTagEntity;
import com.prosilion.superconductor.entity.standard.AddressTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityAddressTagEntityRepository;
import com.prosilion.superconductor.repository.standard.AddressTagEntityRepository;
import lombok.NonNull;
import nostr.event.tag.AddressTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
