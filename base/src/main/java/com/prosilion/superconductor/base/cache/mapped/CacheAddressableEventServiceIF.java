package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

public interface CacheAddressableEventServiceIF<T extends AddressableEvent> {
  T materialize(@NonNull EventIF eventIF);
  Optional<T> getAddressTagEvent(@NonNull AddressTag addressTag) throws NostrException;
  Kind getKind();
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
