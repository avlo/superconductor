package com.prosilion.superconductor.lib.redis.interceptor;

import com.prosilion.nostr.tag.ExternalIdentityTag;
import org.springframework.stereotype.Component;

@Component
public class ExternalIdentityTagInterceptor<T extends ExternalIdentityTag, U extends RedisExternalIdentityTag> implements TagInterceptor<T, U> {

  @Override
  public U intercept(T externalIdentityTag) {
    return (U) new RedisExternalIdentityTag(externalIdentityTag.getKind(), externalIdentityTag.identifierTag(), externalIdentityTag.getFormula());
  }

  @Override
  public T canonicalize(U redisExternalIdentityTag) {
    return (T) new ExternalIdentityTag(redisExternalIdentityTag.getKind(), redisExternalIdentityTag.getIdentifierTag(), redisExternalIdentityTag.getFormula());
  }

  @Override
  public String getCode() {
    return "i";
  }
}
