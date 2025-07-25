package com.prosilion.superconductor.lib.redis.interceptor;

import com.prosilion.nostr.tag.BaseTag;

public interface TagInterceptor<T extends BaseTag, U extends RedisBaseTagIF> {
  U intercept(T t);
  T canonicalize(U baseTag);
  String getCode();
}
