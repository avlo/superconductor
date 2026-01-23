package com.prosilion.superconductor.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.util.NostrRelayService;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

public interface CacheDereferenceAbstractTagServiceIF<T extends ReferencedAbstractEventTag> {
  Optional<GenericEventRecord> getEvent(T t);

  @SneakyThrows
  default <S extends BaseEvent> S createTypedFxnEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<S> baseEventFromKind,
      @NonNull Function<T, ? extends BaseEvent> exampleFunction) {
    Constructor<S> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class, Function.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return constructor.newInstance(genericEventRecord, exampleFunction);
  }

  default Supplier<Optional<GenericEventRecord>> remoteEventSupplier(
      @NonNull String recommendedRelayUrl,
      @NonNull EventTag baseTag,
      @NonNull Function<EventTag, Filters> fxnFilter) {

    NostrRelayService relayService = new NostrRelayService(recommendedRelayUrl);
    Supplier<Optional<GenericEventRecord>> supplier = () -> {
      try {
        List<GenericEventRecord> first = getGenericEvents(
            relayService.send(
                createSuperconductorReqMessageRxR(
                    generateRandomHex64String(),
                    baseTag,
                    fxnFilter)));
        relayService.disconnect();
        Optional<GenericEventRecord> eventRecord = first.stream().findFirst();
        return eventRecord;
      } catch (JsonProcessingException e) {
        throw new NostrException(e);
      }
    };
    return supplier;
  }

  default ReqMessage createSuperconductorReqMessageRxR(
      @NonNull String subscriberId,
      @NonNull EventTag baseTag,
      @NonNull Function<EventTag, Filters> fxnFilter) {
    Filters apply = fxnFilter.apply(baseTag);
    ReqMessage reqMessage = new ReqMessage(subscriberId, apply);
    return reqMessage;
  }
  
  default ReqMessage createSuperconductorReqMessage(
      @NonNull String subscriberId,
      @NonNull T baseTag,
      @NonNull Function<T, Filters> fxnFilter) {
    Filters apply = fxnFilter.apply(baseTag);
    ReqMessage reqMessage = new ReqMessage(subscriberId, apply);
    return reqMessage;
  }

  default List<GenericEventRecord> getGenericEvents(List<BaseMessage> returnedBaseMessages) {
    List<GenericEventRecord> list = returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .map(eventIF -> new GenericEventRecord(
            eventIF.getId(),
            eventIF.getPublicKey(),
            eventIF.getCreatedAt(),
            eventIF.getKind(),
            eventIF.getTags(),
            eventIF.getContent(),
            eventIF.getSignature()))
        .toList();
    return list;
  }

  default String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", "");
  }
}
