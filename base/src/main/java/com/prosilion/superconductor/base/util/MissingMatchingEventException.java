package com.prosilion.superconductor.base.util;

import com.prosilion.nostr.NostrException;
import java.util.List;

public class MissingMatchingEventException extends NostrException {
  public static final String MISSING_MATCHING_EVENT_S = "Event's EventTag id's: [%s] missing matching EventTagEvent [%s]";

  public static final String MISSING_MATCHING_EVENT_TAG_S = "EventTagEvent id's: [%s] missing matching Event [%s]";

  public MissingMatchingEventException(List<String> eventTagIds, boolean eventTagMode) {
    this(eventTagIds, "", eventTagMode);
  }

  public MissingMatchingEventException(List<String> eventTagIds, String message, boolean eventTagMode) {
    super(String.format(
        eventTagMode ? MISSING_MATCHING_EVENT_S : MISSING_MATCHING_EVENT_TAG_S, eventTagIds, message));
  }
}
