package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.RequestSubscriber;
import com.prosilion.subdivisions.client.virtualthread.VThreadWebSocketClient;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import com.prosilion.superconductor.base.util.RemoteEventQueryException;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteAbstractTagService implements RemoteEventQueryServiceIF {
  private static final Duration DEFAULT_WAIT_DURATION = Duration.ofSeconds(10);
  private final Duration waitDuration;

  public RemoteAbstractTagService() {
    this(DEFAULT_WAIT_DURATION);
  }

  public RemoteAbstractTagService(@NonNull Duration waitDuration) {
    this.waitDuration = waitDuration;
  }

  @Override
  public List<GenericEventRecord> sendRemoteReq(
     @NonNull String relayUrl,
     @NonNull Filters filters) {
    ReqMessage reqMessage = new ReqMessage(Util.generateRandomHex64String(), filters);
    log.debug(
       "Querying relay [{}] with subscriber [{}] and filters:\n{}",
       relayUrl,
       reqMessage.getSubscriptionId(),
       filters.toString(4));

    List<GenericEventRecord> events = getGenericEvents(
       awaitUsingWebSocketClient(reqMessage, relayUrl));
    log.debug("Relay [{}] returned [{}] events", relayUrl, events.size());
    return events;
  }

  private List<GenericEventRecord> getGenericEvents(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
       .filter(EventMessage.class::isInstance)
       .map(EventMessage.class::cast)
       .map(EventMessage::getEvent)
       .map(EventIF::asGenericEventRecord)
       .toList();
  }

  private List<BaseMessage> awaitUsingWebSocketClient(ReqMessage reqMessage, String relayUrl) {
    try {
      VThreadWebSocketClient vThreadWebSocketClient = new VThreadWebSocketClient(relayUrl);
      vThreadWebSocketClient.send(reqMessage);
      RequestSubscriber.await(waitDuration, () -> !vThreadWebSocketClient.getEvents().isEmpty());
      return vThreadWebSocketClient.getPopulatedEvents().stream()
         .map(this::decodeMessage)
         .toList();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RemoteEventQueryException(
         String.format("Interrupted while querying relay [%s]", relayUrl), e);
    } catch (ExecutionException | IOException e) {
      throw new RemoteEventQueryException(
         String.format("Failed to query relay [%s]", relayUrl), e);
    }
  }

  private BaseMessage decodeMessage(String message) {
    try {
      return BaseMessageDecoder.decode(message);
    } catch (JsonProcessingException e) {
      throw new RemoteEventQueryException("Failed to decode relay response", e);
    }
  }
}
