package com.prosilion.superconductor.base.util;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.filter.Filters;
import java.util.List;

public class EmptyFiltersException extends NostrException {
  public static final String FILTERS_EXCEPTION = "Invalid filters %s, missing [%s]";

  public EmptyFiltersException(List<Filters> filtersList, String message) {
    super(String.format(FILTERS_EXCEPTION, filtersList, message));
  }
}
