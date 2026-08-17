package java.com.prosilion.superconductor.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.prosilion.nostr.util.Util.generateRandomHex64String;
import static java.com.prosilion.superconductor.BaseFollowSetsEventServiceIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedFormulaEventMessageIT extends BaseIntegrationTestFixtures {
  protected final String formulaEventRelayUrl;
  protected final Relay formulaEventRelay;
  protected final CacheServiceIF cacheServiceIF;

  protected final List<FormulaEvent> formulaEventList;

  abstract protected List<FormulaEvent> createFormulaEventList();

  public AbstractBaseCacheCuratedFormulaEventMessageIT(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.cacheServiceIF = cacheServiceIF;
    this.formulaEventRelayUrl = superconductorRelayUrl;
    this.formulaEventRelay = new Relay(superconductorRelayUrl);

    this.formulaEventList = createFormulaEventList();
    setupFormulaEvents(formulaEventList);
  }

  private void setupFormulaEvents(List<FormulaEvent> formulaEvents) {
    formulaEvents.stream()
       .map(FormulaEvent::getBadgeDefinitionGenericEvent)
       .forEach(this::setupBadgeDefinitionEvent);

    formulaEvents.forEach(formulaEvent -> assertTrue(
       new NostrEventPublisher(formulaEventRelayUrl)
          .send(
             new EventMessage(formulaEvent), Duration.ofSeconds(10)).getFlag()));

    overridableValidateCorrectlyCreatedAndPersistedFormulaEventVariant(formulaEvents);
  }

  private void setupBadgeDefinitionEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    assertTrue(
       new NostrEventPublisher(formulaEventRelayUrl)
          .send(
             new EventMessage(badgeDefinitionGenericEvent))
          .getFlag());

    overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(badgeDefinitionGenericEvent);
  }

  public void overridableValidateCorrectlyCreatedAndPersistedFormulaEventVariant(List<FormulaEvent> formulaEventList) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.ARBITRARY_CUSTOM_APP_DATA))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> sanityCheckFormulaEventIds = returnedEventIFs.stream().map(EventIF::getId).collect(Collectors.toSet());

    assertTrue(sanityCheckFormulaEventIds.stream().anyMatch(
       formulaEventList.stream().map(FormulaEvent::getBadgeDefinitionGenericEvent)
          .map(BaseEvent::getId).toList()::contains));
  }

  public void validateCorrectlyCreatedAndPersistedCuratedFormulaEventVariants(List<FormulaEvent> formulaEvents) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> eventIds = returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).collect(Collectors.toSet());

    assertTrue(eventIds.stream().anyMatch(formulaEvents.stream().map(FormulaEvent::getId).toList()::contains));
  }

  protected void validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> eventIds = returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).collect(Collectors.toSet());

    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }

  public void overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.BADGE_DEFINITION_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> eventIds = returnedEventIFs.stream().map(EventIF::getId).collect(Collectors.toSet());
    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }

  @Test
  void testExpectedEvent() throws NostrException {
    List<EventIF> returnedCuratedFormulaEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedFormulaEvents);

    List<String> eventIds = returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.stream().anyMatch(this.formulaEventList.stream().map(FormulaEvent::getId).toList()::contains));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(PubKeyTag.class)).map(PubKeyTag::getPublicKey)
       .allMatch(formulaCreator.getPublicKey()::equals));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(AddressTag.class))
       .anyMatch(
          this.formulaEventList.stream().map(FormulaEvent::asAddressableEventAddressTag).toList()::contains));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class)).map(EventTag::eventId)
       .anyMatch(
          this.formulaEventList.stream().map(FormulaEvent::getId).toList()::contains));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(GenericEventRecord::getContent)
       .anyMatch(
          this.formulaEventList.stream().map(FormulaEvent::getFormula).toList()::contains));
  }
}
