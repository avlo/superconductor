package com.prosilion.superconductor.base.service.event.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import java.util.List;
import lombok.NonNull;

public interface EventKindServiceIF {
  void processIncomingEvent(@NonNull EventIF event, @NonNull Relay relay);
  List<Kind> getKinds();
}
