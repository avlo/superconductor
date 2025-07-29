package com.prosilion.superconductor.lib.jpa.entity;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import java.util.List;

public interface EventEntityIF extends EventIF {
  Long getId();
  void setTags(List<BaseTag> tags);
  default String getPubKey() {
    return getPublicKey().toString();
  }
}
