package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventPlugin implements EventPluginIF {
  private final static String CLASS_STRING_MAP_S = "Class [%s] not found in kindClassStringMap [%s]";

  @Getter private final CacheServiceIF cacheServiceIF;
  private final Map<String, String> kindClassStringMap;
  private final Map<Kind, Function<EventIF, BaseEvent>> eventKindMaterializers;
  private final Map<Kind, Function<EventIF, BaseEvent>> eventKindTypeMaterializers;

  public EventPlugin(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull Map<Kind, Function<EventIF, BaseEvent>> eventKindMaterializers,
      @NonNull Map<Kind, Function<EventIF, BaseEvent>> eventKindTypeMaterializers,
      @NonNull Map<String, String> kindClassStringMap) {
    log.debug("class is adding class: {}", cacheServiceIF.getClass().getSimpleName());
    this.cacheServiceIF = cacheServiceIF;
    this.eventKindMaterializers = eventKindMaterializers;
    this.eventKindTypeMaterializers = eventKindTypeMaterializers;
    this.kindClassStringMap = kindClassStringMap;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    log.debug("processIncomingEvent() called with event\n{}", event.createPrettyPrintJson());
    Optional<EventIF> eventAlreadyExists = eventAlreadyExistsFxn.apply(cacheServiceIF, event);
    if (eventAlreadyExists.isPresent()) {
      System.out.println("00000000000000000000000000");
      System.out.println("00000000000000000000000000");
      System.out.println("00000000000000000000000000");
      log.debug("event already exists in db, do not materialize, just return\n{}", event.createPrettyPrintJson());
      return;
    }

    boolean isEventKind = eventKindMaterializers.containsKey(event.getKind());
    boolean isEventKindType = eventKindTypeMaterializers.containsKey(event.getKind());
    if (isEventKind || isEventKindType) {
      System.out.println("11111111111111111111111111");
      System.out.println("11111111111111111111111111");
      System.out.println("11111111111111111111111111");
      log.debug("kind/kindType event does not yet exist in db, materialize...");
      BaseEvent apply = getEventKindFxn(event).apply(event);
      log.debug("kind/kindType event materialized...\n{}", apply.createPrettyPrintJson());
      cacheServiceIF.save(apply);
      return;
    }

    System.out.println("22222222222222222222222222");
    System.out.println("22222222222222222222222222");
    System.out.println("22222222222222222222222222");
//    log.debug("creating canonical kind event...\n{}", event.createPrettyPrintJson());
    BaseEvent typedEvent = createTypedEvent(event).orElseThrow();
//    log.debug("canonical kind event created...\n{}", typedEvent.createPrettyPrintJson());
    cacheServiceIF.save(typedEvent);
  }

  Function<EventIF, BaseEvent> getEventKindFxn(EventIF eventIF) {
    Kind kind = eventIF.getKind();

    Optional<ExternalIdentityTag> externalIdentityTagOptional = Filterable.getTypeSpecificTagsStream(ExternalIdentityTag.class, eventIF).findFirst();

    boolean containsKindKey = eventKindMaterializers.containsKey(kind);
    if (containsKindKey) {
      boolean containsKindTypeKey = eventKindTypeMaterializers.containsKey(kind);
      if (containsKindTypeKey) {
        boolean identityTagOptionalPresent = externalIdentityTagOptional.isPresent();
        if (identityTagOptionalPresent)
          return eventKindTypeMaterializers.get(kind);
      }
    }
    return eventKindMaterializers.get(kind);
  }

  Function<EventIF, BaseEvent> getEventKindTypeFxn(EventIF eventIF) {
    Kind kind = eventIF.getKind();

    Optional<ExternalIdentityTag> externalIdentityTagOptional = Filterable.getTypeSpecificTagsStream(ExternalIdentityTag.class, eventIF).findFirst();

    boolean containsKindKey = eventKindMaterializers.containsKey(kind);
    if (containsKindKey) {
      boolean containsKindTypeKey = eventKindTypeMaterializers.containsKey(kind);
      if (containsKindTypeKey) {
        boolean identityTagOptionalPresent = externalIdentityTagOptional.isPresent();
        if (identityTagOptionalPresent)
          return eventKindTypeMaterializers.get(kind);
      }
    }
    return eventKindMaterializers.get(kind);
  }

  public Optional<BaseEvent> createTypedEvent(EventIF eventIF) {
    String lookupKind = eventIF.getKind().getName().toUpperCase();
    Optional<String> optionalLookupKind = Optional.ofNullable(kindClassStringMap.get(lookupKind));

    if (optionalLookupKind.isEmpty())
      return Optional.empty();

    Class<? extends BaseEvent> aClass = null;
    try {
      aClass = (Class<? extends BaseEvent>) Class.forName(optionalLookupKind.get());
    } catch (ClassNotFoundException e) {
      throw lookupKindNotFound(lookupKind);
    }

    Class<? extends BaseEvent> finalAClass = aClass;
    Optional<BaseEvent> baseEvent = optionalLookupKind.map(s ->
        createTypedSimpleEvent(
            eventIF.asGenericEventRecord(),
            finalAClass));

    return baseEvent;
  }

  private NostrException lookupKindNotFound(String lookupKind) {
    return new NostrException(
        String.format(CLASS_STRING_MAP_S, lookupKind, kindClassStringMap));
  }

  @SneakyThrows
  private <T extends BaseEvent> T createTypedSimpleEvent(
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
