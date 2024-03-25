package com.prosilion.nostrrelay.service;

import nostr.event.BaseTag;
import nostr.event.impl.GenericTag;

import java.util.List;
public interface ClassifiedEventService {
  default String getTagValue(List<BaseTag> tags, int index) {
    return (String) ((GenericTag) tags.get(index)).getAttributes().get(0).getValue();
  }
}
