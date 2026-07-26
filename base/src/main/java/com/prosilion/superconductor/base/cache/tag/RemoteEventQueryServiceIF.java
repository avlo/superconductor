package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import java.util.List;
import lombok.NonNull;

public interface RemoteEventQueryServiceIF {
  List<GenericEventRecord> sendRemoteReq(@NonNull String relayUrl, @NonNull Filters filters);
}
