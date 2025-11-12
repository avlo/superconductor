package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.NonNull;

public class CacheKindClassMapService {
  Map<Kind, CacheEventTagBaseEventServiceIF> kindClassMap = new HashMap<>();

  public CacheKindClassMapService(@NonNull List<CacheEventTagBaseEventServiceIF> cacheEventTagBaseEventServiceIFS) {
    cacheEventTagBaseEventServiceIFS.forEach(kindClassMap ->
        add(kindClassMap.getKind(), kindClassMap));
  }

  public void add(@NonNull Kind kind, @NonNull CacheEventTagBaseEventServiceIF value) {
    kindClassMap.putIfAbsent(kind, value);
  }
}
