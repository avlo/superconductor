package com.prosilion.superconductor.lib.redis.interceptor;

import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import org.springframework.stereotype.Component;

@Component
public class PubKeyTagInterceptor<T extends PubKeyTag, U extends RedisPubKeyTag> implements TagInterceptor<T, U> {

  @Override
  public U intercept(T pubKeyTag) {
    return (U) new RedisPubKeyTag(pubKeyTag.getPublicKey().toString(), pubKeyTag.getMainRelayUrl());
  }

  @Override
  public T revert(U redisPubKeyTag) {
    return (T) new PubKeyTag(
        new PublicKey(
            redisPubKeyTag.getPublicKey()),
        redisPubKeyTag.getMainRelayUrl(),
        redisPubKeyTag.getPetName());
  }

  @Override
  public String getCode() {
    return "p";
  }
}
