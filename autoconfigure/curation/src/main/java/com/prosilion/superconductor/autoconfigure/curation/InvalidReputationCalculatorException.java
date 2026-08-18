package com.prosilion.superconductor.autoconfigure.curation;

import com.prosilion.nostr.NostrException;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

public class InvalidReputationCalculatorException extends NostrException {
  public static final String message = "Reputation calculator [%s] not found in available calculators [%s]";

  public InvalidReputationCalculatorException(String invalidCalculator, List<String> validCalculators) {
    super(String.format(message, invalidCalculator, Strings.join(validCalculators, ',')));
  }
}
