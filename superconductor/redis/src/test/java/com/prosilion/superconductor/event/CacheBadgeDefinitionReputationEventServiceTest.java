package com.prosilion.superconductor.event;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CacheBadgeDefinitionReputationEventServiceTest extends CacheServiceTestFixture<BadgeDefinitionReputationEvent> {
  @Mock
  CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  @Mock
  CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  @Mock
  CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  @Mock
  CacheKindAddressTagService cacheKindAddressTagService;
  @Mock
  CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService;

  CuratedFormulaEvent curatedFormulaEvent;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       null,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheCuratedFormulaEventServiceIF,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       null,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheCuratedBadgeDefinitionGenericEventService,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheBadgeDefinitionReputationEventService service = createService();

    assertThrows(NullPointerException.class, () -> service.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> service.getEvent(eventId, null));
  }

  @Test
  void testGetByExpandedRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createService().getByExpanded(null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createService().getByDirect(null));
  }

  @SneakyThrows
  @Override
  protected BadgeDefinitionReputationEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent =
       new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);

    this.curatedFormulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, badgeDefinitionGenericEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    return new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       curatedFormulaEvent);
  }

  private CacheBadgeDefinitionReputationEventService createService() {
    return new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheKindAddressTagService);
  }
}
