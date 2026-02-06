package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.lang.reflect.Constructor;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

public interface EventPluginIF {
  <T extends BaseEvent> void processIncomingEvent(T event);

  @SneakyThrows
  default  <T extends BaseEvent> T createTypedSimpleEvent(
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

  @SneakyThrows
  default <S extends ReferencedAbstractEventTag, T extends BaseEvent> T createTypedFxnEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind,
      @NonNull Function<S, ? extends BaseEvent> exampleFunction) {
    Constructor<T> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class, Function.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return constructor.newInstance(genericEventRecord, exampleFunction);
  }
}
