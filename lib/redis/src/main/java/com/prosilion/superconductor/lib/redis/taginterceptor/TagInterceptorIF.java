package com.prosilion.superconductor.lib.redis.taginterceptor;

import com.prosilion.nostr.tag.BaseTag;

public interface TagInterceptorIF<T extends BaseTag> {
  BaseTag intercept(T t);
  Class<T> getInterceptorType();
}
