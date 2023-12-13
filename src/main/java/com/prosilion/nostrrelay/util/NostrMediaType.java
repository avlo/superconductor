package com.prosilion.nostrrelay.util;

import org.springframework.util.MimeType;

import java.io.Serializable;

public class NostrMediaType extends MimeType implements Serializable {
  private static final long serialVersionUID = -8041839550700514103L;
  public static final NostrMediaType APPLICATION_NOSTR_JSON = new NostrMediaType("application", "nostr+json");
  public static final String APPLICATION_NOSTR_JSON_VALUE = "application/nostr+json";

  public NostrMediaType(String type, String subtype) {
    super(type, subtype);
  }
}
