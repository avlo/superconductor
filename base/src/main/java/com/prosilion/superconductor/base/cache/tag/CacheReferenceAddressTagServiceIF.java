package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import java.util.List;
import org.springframework.lang.NonNull;

public interface CacheReferenceAddressTagServiceIF extends CacheReferenceAbstractTagServiceIF<AddressTag> {

  List<GenericEventRecord> getEventAddressTagsAsGenericEventRecords(@NonNull EventIF eventIF);
}
