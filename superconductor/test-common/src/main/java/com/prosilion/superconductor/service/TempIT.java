package com.prosilion.superconductor.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.util.Factory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class TempIT {
  private static final Relay relay = new Relay("ws://localhost:5555");
  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final CacheFormulaEventService cacheFormulaEventService;
  private final EventPluginIF eventPluginIF;
  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
  private final FormulaEvent formulaEvent;

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
  public final String PLUS_ONE_FORMULA = "+1";

  public TempIT(
      EventPluginIF eventPluginIF,
      CacheEventTagBaseEventServiceIF cacheFormulaEventService) throws ParseException {
    this.eventPluginIF = eventPluginIF;
    this.cacheFormulaEventService = (CacheFormulaEventService) cacheFormulaEventService;
    this.badgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(IDENTITY, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);

    this.formulaEvent =
        new FormulaEvent(
            IDENTITY,
            badgeDefinitionAwardEvent,
            PLUS_ONE_FORMULA);
  }

  @Test
  @Order(1)
  void testNonExistentEventTag() throws ParseException {
    assertTrue(
        assertThrows(NostrException.class, () ->
            eventPluginIF.processIncomingEvent(this.formulaEvent))
            .getMessage().contains(
                Strings.concat(
                    String.format(CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S, formulaEvent.getId()),
                    String.format("[%s]", badgeDefinitionAwardEvent.getId()))));

    eventPluginIF.processIncomingEvent(this.badgeDefinitionAwardEvent);
    eventPluginIF.processIncomingEvent(this.formulaEvent);
    FormulaEvent savedFormulaEvent = cacheFormulaEventService.getEventByEventId(formulaEvent.getId()).orElseThrow();
    assertNotNull(savedFormulaEvent);

    log.info("saved id: {}", savedFormulaEvent);
  }
}
