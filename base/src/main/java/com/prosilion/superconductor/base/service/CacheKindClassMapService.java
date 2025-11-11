package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.NonNull;

public class CacheKindClassMapService<T extends BaseEvent> {
  Map<Kind, CacheEventMapServiceIF<T>> kindClassMap = new HashMap<>();

  public CacheKindClassMapService(@NonNull Map<String, CacheEventMapServiceIF<T>> kindClassStringMap) throws ClassNotFoundException {
    for (Map.Entry<String, CacheEventMapServiceIF<T>> entry : kindClassStringMap.entrySet()) {
      add(Kind.valueOf(entry.getKey()), entry.getValue());
    }
  }

  public void add(@NonNull Kind kind, @NonNull CacheEventMapServiceIF<T> value) throws ClassNotFoundException {
    kindClassMap.putIfAbsent(kind, value);
  }
}
