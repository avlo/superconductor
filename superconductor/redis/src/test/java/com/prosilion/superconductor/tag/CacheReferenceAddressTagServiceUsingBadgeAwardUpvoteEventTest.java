package com.prosilion.superconductor.tag;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheReferenceAddressTagServiceUsingBadgeAwardUpvoteEventTest extends CacheServiceTestFixture<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
  @Test
  void testGetEventByAddressTag() {
    AddressTag addressTag = event.getAddressTag();
    mockLocalGetEventByAddressTag(addressTag);

    CacheReferenceAddressTagService cacheReferenceAddressTagService =
       new CacheReferenceAddressTagService(cacheServiceIF, remoteAbstractTagService);

    String actualEventIdViaAddressTagService = cacheReferenceAddressTagService
       .getByExpanded(addressTag).orElseThrow().getId();
    assertEquals(eventId, actualEventIdViaAddressTagService);

    verify(cacheServiceIF, Mockito.times(1)).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       addressTag.getKind(), addressTag.publicKey(), addressTag.requireIdentifierTag());
  }

  @Test
  void testGetEventByAddressTagCalledOnce() {
    AddressTag addressTag = event.getAddressTag();
    mockLocalGetEventByAddressTag(addressTag);

    CacheReferenceAddressTagService cacheReferenceAddressTagService =
       new CacheReferenceAddressTagService(cacheServiceIF, remoteAbstractTagService);

    cacheReferenceAddressTagService.getByExpanded(addressTag);
    verify(cacheServiceIF, Mockito.times(1)).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       any(), any(), any());
  }

  @Test
  void testGetEventByNonExistentAddressTagReturnsEmptyOptional() {
    AddressTag addressTag = event.getAddressTag();
    mockLocalGetEventByAnyAddressTagReturnsEmptyOptional();

    CacheReferenceAddressTagService cacheReferenceAddressTagService =
       new CacheReferenceAddressTagService(cacheServiceIF, remoteAbstractTagService);

    Optional<GenericEventRecord> actual = cacheReferenceAddressTagService.getByExpanded(addressTag);

    verify(cacheServiceIF, Mockito.times(1)).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       any(), any(), any());
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(
       anyString(), any(Filters.class));

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetEventByNonExistentAddressTagReturnsRemoteObject() {
    AddressTag addressTag = event.getAddressTag();
    mockLocalGetEventByAnyAddressTagReturnsEmptyOptional();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(remoteAbstractTagService)
       .sendRemoteReq(eq(relay.getUrl()), any(Filters.class));

    CacheReferenceAddressTagService cacheReferenceAddressTagService =
       new CacheReferenceAddressTagService(cacheServiceIF, remoteAbstractTagService);

    String actualEventIdViaAddressTagService = cacheReferenceAddressTagService
       .getByExpanded(addressTag).orElseThrow().getId();

    assertEquals(eventId, actualEventIdViaAddressTagService);
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(
       eq(relay.getUrl()), any(Filters.class));
  }

  @Override
  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createEvent() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          relay));
  }

  private void mockLocalGetEventByAddressTag(AddressTag addressTag) {
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventByKindAndAuthorPublicKeyAndIdentifierTag(
          eq(addressTag.getKind()), eq(addressTag.publicKey()), eq(addressTag.requireIdentifierTag()));
  }

  private void mockLocalGetEventByAnyAddressTagReturnsEmptyOptional() {
    doReturn(Optional.empty())
       .when(cacheServiceIF)
       .getEventByKindAndAuthorPublicKeyAndIdentifierTag(any(), any(), any());
  }
}
