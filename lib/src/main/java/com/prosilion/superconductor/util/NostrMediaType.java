package com.prosilion.superconductor.util;

import org.springframework.util.MimeType;

import java.io.Serial;
import java.io.Serializable;

public class NostrMediaType extends MimeType implements Serializable {
  @Serial
  private static final long serialVersionUID = -8041839550700514103L;
  public static final NostrMediaType APPLICATION_NOSTR_JSON = new NostrMediaType("application", "nostr+json");

  public NostrMediaType(String type, String subtype) {
    super(type, subtype);
  }
}
