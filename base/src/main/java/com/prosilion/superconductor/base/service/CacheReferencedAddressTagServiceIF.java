package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;

public interface CacheReferencedAddressTagServiceIF extends CacheReferencedAbstractTagServiceIF<AddressTag> {
  @Override
  Optional<GenericEventRecord> getEvent(AddressTag t);
}
