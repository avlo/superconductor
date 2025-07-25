package com.prosilion.superconductor.lib.redis.interceptor;

import com.prosilion.nostr.tag.BaseTag;

public interface RedisBaseTagIF {
//  TODO: remove below, unused
  BaseTag intercept(BaseTag baseTag);
  BaseTag revert(BaseTag baseTag);
  String getCode();
}
