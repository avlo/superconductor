package com.prosilion.superconductor.base.cache.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.util.NostrRelayService;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
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

  default Optional<GenericEventRecord> remoteEventSupplier(
      @NonNull String recommendedRelayUrl,
      @NonNull T referencedAbstractEventTag,
      @NonNull Function<T, Filters> fxnFilter) {
    NostrRelayService relayService = new NostrRelayService(recommendedRelayUrl);

    try {
      Optional<GenericEventRecord> eventRecord =
          getGenericEvents(
              relayService.send(
                  new ReqMessage(
                      generateRandomHex64String(),
                      fxnFilter.apply(referencedAbstractEventTag))))
              .findFirst();
      relayService.disconnect();
      return eventRecord;
    } catch (JsonProcessingException e) {
      throw new NostrException(e);
    }
  }

  default Stream<GenericEventRecord> getGenericEvents(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .map(EventIF::asGenericEventRecord);
  }

  default String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", "");
  }
}
