package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.Optional;

public interface EventKindPluginIF extends EventPluginIF {
  Kind getKind();
}
