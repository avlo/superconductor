package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.util.Optional;
import lombok.NonNull;

public interface CacheReferenceAbstractTagServiceIF<T extends ReferencedAbstractEventTag> {
  Optional<GenericEventRecord> getByExpanded(@NonNull T tag);
}
