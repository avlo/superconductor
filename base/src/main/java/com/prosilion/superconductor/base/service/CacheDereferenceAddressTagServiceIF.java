package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;

public interface CacheDereferenceAddressTagServiceIF extends CacheDereferenceAbstractTagServiceIF<AddressTag> {
  @Override
  Optional<GenericEventRecord> getEvent(AddressTag t);
}
