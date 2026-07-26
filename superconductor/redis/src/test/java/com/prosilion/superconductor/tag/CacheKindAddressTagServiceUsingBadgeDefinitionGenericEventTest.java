package com.prosilion.superconductor.tag;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheKindAddressTagServiceUsingBadgeDefinitionGenericEventTest extends CacheServiceTestFixture<BadgeDefinitionGenericEvent> {
  private static final Kind KIND = Kind.BADGE_DEFINITION_EVENT;

  @Test
  void testGetByDirectWithAddressTag() {
    AddressTag addressTag = event.asAddressableEventAddressTag();
    mockLocalGetEventsByKindAndAddressTag(addressTag);

    CacheKindAddressTagService cacheKindAddressTagService =
       new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);

    List<GenericEventRecord> actual = cacheKindAddressTagService.getByDirect(KIND, addressTag);

    assertEquals(List.of(event.getGenericEventRecord()), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndAddressTag(KIND, addressTag);
  }

  @Test
  void testGetByDirectWithAddressTagReturnsRemoteObjects() {
    AddressTag addressTag = event.asAddressableEventAddressTag();
    mockLocalGetEventsByKindAndAddressTagReturnsEmptyList();
    mockRemoteGetEventByAddressTag();

    CacheKindAddressTagService cacheKindAddressTagService =
       new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);

    List<GenericEventRecord> actual = cacheKindAddressTagService.getByDirect(KIND, addressTag);

    assertEquals(List.of(event.getGenericEventRecord()), actual);
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(
       eq(relay.getUrl()), any(Filters.class));
  }

  @Test
  void testGetByDirectWithPubKeyAndAddressTag() {
    AddressTag addressTag = event.asAddressableEventAddressTag();
    PubKeyTag pubKeyTag = new PubKeyTag(upvoteDefnCreator.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndAddressTag(KIND, pubKeyTag, addressTag);

    CacheKindAddressTagService cacheKindAddressTagService =
       new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);

    List<GenericEventRecord> actual = cacheKindAddressTagService.getByDirect(KIND, pubKeyTag, addressTag);

    assertEquals(List.of(event.getGenericEventRecord()), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndAddressTag(
       KIND, pubKeyTag, addressTag);
  }

  @Test
  void testGetByKindPubKeyAndIdentifierTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(upvoteDefnCreator.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(KIND, pubKeyTag, upvoteIdentifierTag);

    CacheKindAddressTagService cacheKindAddressTagService =
       new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);

    Optional<GenericEventRecord> actual = cacheKindAddressTagService.getBy(
       KIND, pubKeyTag, upvoteIdentifierTag, relay.getUrl());

    assertEquals(Optional.of(event.getGenericEventRecord()), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       KIND, pubKeyTag, upvoteIdentifierTag);
  }

  @Test
  void testGetByKindPubKeyAndIdentifierTagReturnsEmptyOptional() {
    mockLocalGetEventsByKindAndPubKeyAndIdentifierTagReturnsEmptyList();

    CacheKindAddressTagService cacheKindAddressTagService =
       new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);

    Optional<GenericEventRecord> actual = cacheKindAddressTagService.getBy(
       KIND, new PubKeyTag(upvoteDefnCreator.getPublicKey()), upvoteIdentifierTag, relay.getUrl());

    assertEquals(Optional.empty(), actual);
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(
       eq(relay.getUrl()), any(Filters.class));
  }

  @Test
  void testGetByKindPubKeyAndIdentifierTagReturnsRemoteObject() {
    PubKeyTag pubKeyTag = new PubKeyTag(upvoteDefnCreator.getPublicKey());
    mockLocalGetEventsByKindAndPubKeyAndIdentifierTagReturnsEmptyList();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(remoteAbstractTagService)
       .sendRemoteReq(eq(relay.getUrl()), any(Filters.class));

    CacheKindAddressTagService cacheKindAddressTagService =
       new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);

    Optional<GenericEventRecord> actual = cacheKindAddressTagService.getBy(
       KIND, pubKeyTag, upvoteIdentifierTag, relay.getUrl());

    assertEquals(Optional.of(event.getGenericEventRecord()), actual);
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(
       eq(relay.getUrl()), any(Filters.class));
  }

  @Override
  protected BadgeDefinitionGenericEvent createEvent() {
    return new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
  }

  private void mockLocalGetEventsByKindAndAddressTag(AddressTag addressTag) {
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndAddressTag(KIND, addressTag);
  }

  private void mockLocalGetEventsByKindAndAddressTagReturnsEmptyList() {
    doReturn(List.of())
       .when(cacheServiceIF)
       .getEventsByKindAndAddressTag(any(), any());
  }

  private void mockLocalGetEventsByKindAndPubKeyAndIdentifierTagReturnsEmptyList() {
    doReturn(List.of())
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(any(), any(), any());
  }

  private void mockRemoteGetEventByAddressTag() {
    doReturn(List.of(event.getGenericEventRecord()))
       .when(remoteAbstractTagService)
       .sendRemoteReq(anyString(), any(Filters.class));
  }
}
