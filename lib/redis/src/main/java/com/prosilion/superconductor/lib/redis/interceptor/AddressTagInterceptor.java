package com.prosilion.superconductor.lib.redis.interceptor;

import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.user.PublicKey;
import org.springframework.stereotype.Component;

@Component
public class AddressTagInterceptor<T extends AddressTag, U extends RedisAddressTag> implements TagInterceptor<T, U> {

  @Override
  public U intercept(T addressTag) {
    return (U) new RedisAddressTag(addressTag.getKind(), addressTag.publicKey().toString(), addressTag.identifierTag(), addressTag.getRelay());
  }

  @Override
  public T canonicalize(U redisAddressTag) {
    return (T) new AddressTag(redisAddressTag.getKind(), new PublicKey(redisAddressTag.getPublicKey()), redisAddressTag.getIdentifierTag(), redisAddressTag.getRelay());
  }

  @Override
  public String getCode() {
    return "a";
  }
}
