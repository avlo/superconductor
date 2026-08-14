package com.prosilion.superconductor.base.curated;

import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
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

  private final BadgeDefinitionGenericEvent upvoteDefinitionEvent;
  private final BadgeDefinitionGenericEvent downvoteDefinitionEvent;
  private final CuratedFormulaEvent upvoteCuratedFormulaEvent;
  private final FormulaEvent upvoteFormulaEvent;
  private final FormulaEvent downvoteFormulaEvent;

  public BaseCacheCuratedFormulaEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF) {
    super(superconductorInstanceIdentity);
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.relay = new Relay(relayUrl);

    this.upvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
//    cacheServiceIF.save(upvoteDefinitionEvent);

    this.upvoteFormulaEvent = new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       upvoteDefinitionEvent,
       PLUS_ONE_FORMULA,
       relay);
//    cacheServiceIF.save(upvoteFormulaEvent);

    this.upvoteCuratedFormulaEvent = new CuratedFormulaEvent(
       aImgIdentity,
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
       downvoteDefinitionEvent,
       MINUS_ONE_FORMULA,
       relay);
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
    IdentifierTag expectedIdentifierTag = new IdentifierTag("1367861445");

    assertEquals(expectedIdentifierTag, AbstractSetsEvent.hashedAddressTag(upvoteDefinitionEvent.asAddressableEventAddressTag()));
    assertEquals(expectedIdentifierTag, AbstractSetsEvent.hashedAddressTag(upvoteFormulaEvent.getAddressTag()));
    assertEquals(expectedIdentifierTag, upvoteCuratedFormulaEvent.getIdentifierTag());
    
    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
       aImgIdentity.getPublicKey(),
       upvoteCuratedFormulaEvent.getIdentifierTag());

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
       aImgIdentity,
       downvoteFormulaEvent,
       new ReferenceTag(relay.getUrl()),
       relay);

    CuratedFormulaEvent actual = cacheCuratedFormulaEventServiceIF.getByDirect(expected.getEventTag()).orElseThrow();

    assertEquals(MINUS_ONE_FORMULA, actual.getFormula());
    assertEquals(expected.getAddressTag(), actual.getAddressTag());
    assertEquals(downvoteFormulaEvent.getAddressTag(), actual.getAddressTag());
  }

  @Test
  public void testGetByAuthorPublicKeyIdentifierTagAfterLocalMissIsEmpty() {
    assertEquals(
       Optional.empty(),
       cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
          aImgIdentity.getPublicKey(),
          AbstractSetsEvent.hashedAddressTag(downvoteFormulaEvent.getAddressTag())));
  }

  @Test
  public void testGetByDirectAddressTagAfterLocalMiss() {
    CuratedFormulaEvent actual = cacheCuratedFormulaEventServiceIF.getByDirect(
       downvoteFormulaEvent.getAddressTag()).orElseThrow();

    assertEquals(MINUS_ONE_FORMULA, actual.getFormula());
    assertEquals(downvoteFormulaEvent.getAddressTag(), actual.getAddressTag());
  }
}
