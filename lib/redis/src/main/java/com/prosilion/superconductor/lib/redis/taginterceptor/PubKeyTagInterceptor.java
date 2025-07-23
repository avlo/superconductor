package com.prosilion.superconductor.lib.redis.taginterceptor;

import com.prosilion.nostr.tag.PubKeyTag;
import org.springframework.stereotype.Component;

@Component
public class PubKeyTagInterceptor<T extends PubKeyTag> implements TagInterceptorIF<T> {
  @Override
  public RedisPubKeyTag intercept(T pubKeyTag) {
    return new RedisPubKeyTag(pubKeyTag.getPublicKey().toString(), pubKeyTag.getMainRelayUrl());
  }

  @Override
  public Class<T> getInterceptorType() {
    return (Class<T>) PubKeyTag.class;
  }
}
