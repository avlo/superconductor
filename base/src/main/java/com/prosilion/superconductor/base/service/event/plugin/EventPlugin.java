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
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
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
    log.debug("class is adding cacheServiceIF implementation class: {}", cacheServiceIF.getClass().getSimpleName());
    this.cacheServiceIF = cacheServiceIF;
    this.eventKindMaterializers = eventKindMaterializers;
    this.eventKindTypeMaterializers = eventKindTypeMaterializers;
    this.kindClassStringMap = kindClassStringMap;
    log.debug("loaded kindClassStringMap:");
    this.kindClassStringMap.forEach((key, value) -> log.debug("  {} : {}", key.toUpperCase(), value));
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    log.debug("processIncomingEvent() called with event\n{}", event.createPrettyPrintJson());
    Optional<EventIF> eventAlreadyExists = eventAlreadyExistsFxn.apply(cacheServiceIF, event);
    if (eventAlreadyExists.isPresent()) {
      log.debug("event already exists in db, do not materialize, just return\n  {}\n", event.createPrettyPrintJson());
      return event.asGenericEventRecord();
    }

    boolean isEventKind = eventKindMaterializers.containsKey(event.getKind());
    boolean isEventKindType = eventKindTypeMaterializers.containsKey(event.getKind());
    if (isEventKind || isEventKindType) {
      log.debug("kind/kindType event does not yet exist in db, materialize...\n  {}\n", event.createPrettyPrintJson());
      BaseEvent apply = getEventKindFxn(event).apply(event);
      return cacheServiceIF.save(apply);
    }

    log.debug("creating canonical kind event...\n  {}\n", event.createPrettyPrintJson());
    BaseEvent typedEvent = createTypedEvent(event).orElseThrow();
    return cacheServiceIF.save(typedEvent);
  }

  Function<EventIF, BaseEvent> getEventKindFxn(EventIF eventIF) {
    Kind kind = eventIF.getKind();
    log.debug("getEventKindFxn() for kind\n  [{}]: {}",
        kind.getValue(), kind.getName().toUpperCase());

    Optional<ExternalIdentityTag> externalIdentityTagOptional = Filterable.getTypeSpecificTagsStream(ExternalIdentityTag.class, eventIF).findFirst();

    if (!eventKindTypeMaterializers.containsKey(kind)) {
      Function<EventIF, BaseEvent> eventKindMaterializerFxn = eventKindMaterializers.get(kind);
      log.debug("... eventKindTypeMaterializers did not contain kind, return eventKindMaterializer: ...\n");
      return eventKindMaterializerFxn;
    }

    if (externalIdentityTagOptional.isEmpty()) {
      Function<EventIF, BaseEvent> eventKindMaterializerFxn = eventKindMaterializers.get(kind);
      log.debug("event did not contain externalIdentityTag, return eventKindMaterializer:\n  {}",
          eventKindMaterializerFxn.getClass().getSimpleName());
      return eventKindMaterializerFxn;
    }

    Function<EventIF, BaseEvent> eventKindTypeMaterializerFxn = eventKindTypeMaterializers.get(kind);
    log.debug("return eventKindTypeMaterializer:\n  {}",
        eventKindTypeMaterializerFxn.getClass().getSimpleName());
    return eventKindTypeMaterializerFxn;
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

  private <T extends BaseEvent> T createTypedSimpleEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind) {
    Constructor<T> constructor;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class);
      return constructor.newInstance(genericEventRecord);
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new NostrException(e);
    }
  }
}
