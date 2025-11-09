package com.prosilion.superconductor.lib.redis.service;

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

  public KindClassMapService(Map<String, String> kindClassStringMap) throws ClassNotFoundException {
    for (Map.Entry<String, String> entry : kindClassStringMap.entrySet()) {
      mapEach(Kind.valueOf(entry.getKey()), entry.getValue());
    }
  }

  private void mapEach(Kind kind, String className) throws ClassNotFoundException {
    Class<? extends BaseEvent> value = (Class<? extends BaseEvent>) Class.forName(className);
    kindClassMap.putIfAbsent(kind, value);

  }

  @SneakyThrows
  public BaseEvent createBaseEvent(@NonNull GenericEventRecord genericEventRecord)
//      throws InvocationTargetException, InstantiationException, IllegalAccessException 
  {
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

  Class<? extends BaseEvent> get(Kind kind) {
    return kindClassMap.get(kind);
  }
}
