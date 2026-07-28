package com.prosilion.superconductor.base.curated;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public abstract class BaseCacheCuratedFormulaEventServiceIT extends BaseIntegrationTestFixtures {
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  private final Relay relay;

  private final BadgeDefinitionGenericEvent downvoteDefinitionEvent;
  private final CuratedFormulaEvent upvoteCuratedFormulaEvent;
  private final FormulaEvent downvoteFormulaEvent;

  public BaseCacheCuratedFormulaEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF) throws ParseException {
    super(superconductorInstanceIdentity);
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent upvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
//    cacheServiceIF.save(upvoteDefinitionEvent);

    FormulaEvent upvoteFormulaEvent = new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       relay,
       upvoteDefinitionEvent,
       PLUS_ONE_FORMULA);
//    cacheServiceIF.save(upvoteFormulaEvent);

    this.upvoteCuratedFormulaEvent = new CuratedFormulaEvent(
       parameterAimgIdentity,
       upvoteFormulaEvent,
       new ReferenceTag(relayUrl),
       relay);
    cacheServiceIF.save(upvoteCuratedFormulaEvent);

    this.downvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, downvoteIdentifierTag, relay);
    cacheServiceIF.save(downvoteDefinitionEvent);

    this.downvoteFormulaEvent = new FormulaEvent(
       formulaCreator,
       formulaDownvoteIdentifierTag,
       relay,
       downvoteDefinitionEvent,
       MINUS_ONE_FORMULA);
    cacheServiceIF.save(downvoteFormulaEvent);
  }

  @Test
  public void testGetEventByEventIdRelay() {
    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventServiceIF.getEvent(
       upvoteCuratedFormulaEvent.getId(), relay);

    assertEquals(upvoteCuratedFormulaEvent, actual.orElseThrow());
  }

  @Test
  public void testGetByPublicKeyIdentifierTagAndRelay() {
    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventServiceIF.getBy(
       upvoteCuratedFormulaEvent.getPubKeyTag(),
       formulaUpvoteIdentifierTag,
       relay);

      assertEquals(upvoteCuratedFormulaEvent, actual.orElseThrow());
  }

  @Test
  public void testGetByDirectAddressTag() {
    Optional<CuratedFormulaEvent> actual =
       cacheCuratedFormulaEventServiceIF.getByDirect(upvoteCuratedFormulaEvent.getAddressTag());

    assertEquals(upvoteCuratedFormulaEvent, actual.orElseThrow());
  }

  @Test
  public void testGetByDirectEventTagAfterLocalMiss() {
    CuratedFormulaEvent expected = new CuratedFormulaEvent(
       parameterAimgIdentity,
       downvoteFormulaEvent,
       new ReferenceTag(relay.getUrl()),
       relay);

    CuratedFormulaEvent actual = cacheCuratedFormulaEventServiceIF.getByDirect(expected.getEventTag()).orElseThrow();

    assertEquals(MINUS_ONE_FORMULA, actual.getFormula());
    assertEquals(expected.getAddressTag(), actual.getAddressTag());
    assertEquals(downvoteFormulaEvent.getAddressTag(), actual.getAddressTag());
  }

  @Test
  public void testGetByAuthorPublicKeyIdentifierTagAfterLocalMiss() {
    CuratedFormulaEvent actual = cacheCuratedFormulaEventServiceIF.getBy(
       upvoteCuratedFormulaEvent.getPubKeyTag(),
       formulaDownvoteIdentifierTag,
       relay).orElseThrow();

    assertEquals(MINUS_ONE_FORMULA, actual.getFormula());
    assertEquals(downvoteFormulaEvent.getAddressTag(), actual.getAddressTag());
  }

  @Test
  public void testGetByDirectAddressTagAfterLocalMiss() {
    CuratedFormulaEvent actual = cacheCuratedFormulaEventServiceIF.getByDirect(
       downvoteFormulaEvent.getAddressTag()).orElseThrow();

    assertEquals(MINUS_ONE_FORMULA, actual.getFormula());
    assertEquals(downvoteFormulaEvent.getAddressTag(), actual.getAddressTag());
  }
}
