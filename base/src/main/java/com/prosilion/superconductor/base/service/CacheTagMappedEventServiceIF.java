package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.TagMappedEventIF;
import java.util.Optional;

public interface CacheTagMappedEventServiceIF<T extends TagMappedEventIF> {
  void save(T event);
  Optional<T> getEvent(String eventId);
  Kind getKind();

//  <S extends BaseEvent, T extends ReferentialEventTag> S createEventGivenMappedTag(
//      GenericEventRecord eventIF,
//      Class<S> eventClassFromKind,
//      Function<T, ? extends BaseEvent> fxn);
//
//  @SneakyThrows
//  default <S extends BaseEvent, T extends ReferentialEventTag> S createBaseEvent(
//      @NonNull GenericEventRecord genericEventRecord,
//      @NonNull Class<S> baseEventFromKind,
//      @NonNull Function<T, ? extends BaseEvent> exampleFunction) {
//    assertNotNull(baseEventFromKind);
//    Constructor<S> constructor;
//    try {
//      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class, Function.class);
//    } catch (NoSuchMethodException e) {
//      throw new RuntimeException(e);
//    }
//    return constructor.newInstance(genericEventRecord, exampleFunction);
//  }

}
