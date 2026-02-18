package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.message.EventMessage;
import java.lang.reflect.Constructor;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

public interface EventServiceIF {
  void processIncomingEvent(EventMessage eventMessage);

  @SneakyThrows
  default <T extends BaseEvent> T createTypedSimpleEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind) {
    Constructor<T> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return constructor.newInstance(genericEventRecord);
  }
}
