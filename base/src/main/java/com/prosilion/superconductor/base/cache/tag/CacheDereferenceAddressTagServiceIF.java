package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheDereferenceAddressTagServiceIF extends CacheDereferenceAbstractTagServiceIF<AddressTag> {

  @Override
  Optional<GenericEventRecord> getEvent(AddressTag t);
  List<GenericEventRecord> getEventIFAddressTagsAsGenericEventRecords(@NonNull EventIF eventIF);
}
