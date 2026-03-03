package com.prosilion.superconductor.base.cache.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.awaitility.core.DurationFactory;

public interface CacheDereferenceAddressTagServiceIF extends CacheDereferenceAbstractTagServiceIF<AddressTag> {

  @Override
  default Optional<GenericEventRecord> getEvent(AddressTag t) throws JsonProcessingException {
    return getEvent(t, DurationFactory.of(3, TimeUnit.SECONDS));
  }

  Optional<GenericEventRecord> getEvent(AddressTag t, Duration timeout) throws JsonProcessingException;
}
