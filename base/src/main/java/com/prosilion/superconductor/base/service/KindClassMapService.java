package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.lang.NonNull;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public class KindClassMapService {
  Map<Kind, Class<? extends BaseEvent>> kindClassMap = new HashMap<>();

  public KindClassMapService(@NonNull Map<String, String> kindClassStringMap) throws ClassNotFoundException {
    for (Map.Entry<String, String> entry : kindClassStringMap.entrySet()) {
      add(Kind.valueOf(entry.getKey()), entry.getValue());
    }
  }

  public void add(@NonNull Kind kind, @NonNull String className) throws ClassNotFoundException {
    Class<? extends BaseEvent> value = (Class<? extends BaseEvent>) Class.forName(className);
    kindClassMap.putIfAbsent(kind, value);
  }

  @SneakyThrows
  public BaseEvent createBaseEvent(@NonNull GenericEventRecord genericEventRecord) {
    Class<? extends BaseEvent> baseEventFromKind = kindClassMap.get(genericEventRecord.getKind());
    assertNotNull(baseEventFromKind);

    Constructor<? extends BaseEvent> constructor = null;
    try {
      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }

    BaseEvent event = constructor.newInstance(genericEventRecord);
    return event;

  }
}
