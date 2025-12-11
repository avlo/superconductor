package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

public interface CacheReferencedAbstractTagServiceIF<T extends ReferencedAbstractEventTag> {
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

}
