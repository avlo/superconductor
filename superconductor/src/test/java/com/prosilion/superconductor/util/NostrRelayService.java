package com.prosilion.superconductor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.subdivisions.service.NostrRelayClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.event.message.OkMessage;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

@Slf4j
public class NostrRelayService {
  private final NostrRelayClient nostrRelayService;

  public NostrRelayService(@Value("${superconductor.relay.url}") @NonNull String relayUri) throws ExecutionException, InterruptedException {
    log.debug("relayUri: \n{}", relayUri);
    this.nostrRelayService = new NostrRelayClient(relayUri);
  }

  public NostrRelayService(@Value("${superconductor.relay.url}") @NonNull String relayUri, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
    log.debug("relayUri: \n{}", relayUri);
    log.debug("sslBundles: \n{}", sslBundles);
    final SslBundle server = sslBundles.getBundle("server");
    log.debug("sslBundles name: \n{}", server);
    log.debug("sslBundles key: \n{}", server.getKey());
    log.debug("sslBundles protocol: \n{}", server.getProtocol());
    this.nostrRelayService = new NostrRelayClient(relayUri, sslBundles);
  }

  public OkMessage sendEvent(@NonNull String eventJson) throws IOException {
    return nostrRelayService.sendEvent(eventJson);
  }

  public OkMessage sendEvent(@NonNull EventMessage eventMessage) throws IOException {
    return nostrRelayService.sendEvent(eventMessage);
  }

  public List<GenericEvent> sendRequestReturnEvents(@NonNull ReqMessage reqMessage) throws JsonProcessingException {
    return nostrRelayService.sendRequestReturnEvents(reqMessage);
  }

  public Map<Command, List<String>> sendRequest(@NonNull String reqJson, @NonNull String clientUuid) {
    Map<Command, List<Object>> resultsMap = nostrRelayService.sendRequestReturnCommandResultsMap(clientUuid, reqJson);
    List<Object> objects = resultsMap.get(Command.EVENT);
    List<String> returnedEvents = objects.stream().map(Object::toString).toList();
    log.debug("socket [{}] getEvents():", clientUuid);
    returnedEvents.forEach(event -> log.debug("  {}\n", event));
    log.debug("222222222222\n");
    //    String joined = String.join(",", returnedEvents);
    log.debug("-------------");
    Map<Command, List<String>> returnMap = new HashMap<>();
    returnMap.put(Command.EOSE, List.of(resultsMap.get(Command.EOSE).getFirst().toString()));
    //    Optional<String> value = Optional.of(joined).orElseThrow().isEmpty() ? Optional.empty() : Optional.of(joined);
    returnMap.put(Command.EVENT, returnedEvents);
    return returnMap;
  }
}
