package com.prosilion.superconductor.service.request;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.hash.Hashing;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import com.prosilion.superconductor.service.AbstractSubscriberService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CachedSubscriberService extends AbstractSubscriberService {
  private final Map<Long, List<Combo>> subscriberSessionHashComboMap = Collections.synchronizedMap(new HashMap<>());
  private final BiMap<String, String> biMap = HashBiMap.create();

  @Autowired
  public CachedSubscriberService(ApplicationEventPublisher publisher) {
    super(publisher);
  }

  @Override
  public Long save(@NonNull Subscriber subscriber, @NonNull List<Filters> filtersList) {
    long subscriberSessionHash = getHash(subscriber);
    subscriber.setSubscriberSessionHash(subscriberSessionHash);
    filtersList.forEach(filters -> put(subscriber, filters));
    return subscriberSessionHash;
  }

  //  TODO: list of long???  why not just long
  @Override
  public Map<Long, List<Filters>> getAllFiltersOfAllSubscribers() {
    Map<Long, List<Filters>> map = new HashMap<>();
    subscriberSessionHashComboMap.forEach((key, value) -> map.put(key, value.stream().map(Combo::filters).toList()));
    return map;
  }

//  @Cacheable("subscriber")
  @Override
  public Subscriber get(@NonNull Long subscriberSessionHash) {
    return subscriberSessionHashComboMap.get(subscriberSessionHash).stream().findFirst().get().subscriber();
  }

  @Override
  public List<Filters> getFiltersList(@NonNull Long subscriberSessionHash) {
    return subscriberSessionHashComboMap.get(subscriberSessionHash).stream().map(Combo::filters).toList();
  }

  @Override
  public List<Long> removeSubscriberBySessionId(@NonNull String sessionId) {
    String subscriberId = biMap.inverse().getOrDefault(sessionId, "");
    long hash = getHash(
        new Subscriber(
            subscriberId,
            sessionId,
            true));
    subscriberSessionHashComboMap.remove(hash);
    return List.of(hash);
  }

  @Override
  public Long removeSubscriberBySubscriberId(@NonNull String subscriberId) throws NoExistingUserException {
    long hash = getHash(
        new Subscriber(
            subscriberId,
            biMap.remove(subscriberId),
            true));
    subscriberSessionHashComboMap.remove(hash);
    return hash;
  }

  private void put(Subscriber subscriber, Filters filters) {
    biMap.forcePut(subscriber.getSubscriberId(), subscriber.getSessionId());
    long subscriberSessionHash = getHash(subscriber);

    Combo combo = new Combo(
        subscriber,
        new SubscriberFilter(
            subscriberSessionHash,
            filters.getSince(),
            filters.getUntil(),
            filters.getLimit()),
        filters);

    if (!subscriberSessionHashComboMap.containsKey(subscriberSessionHash)) {
      subscriberSessionHashComboMap.put(subscriberSessionHash, List.of(combo));
      return;
    }
    subscriberSessionHashComboMap.get(subscriberSessionHash).add(combo);
  }

  private long getHash(Subscriber subscriber) {
    return getHash(subscriber.getSubscriberId(), subscriber.getSessionId());
  }

  private long getHash(String subscriberId, String sessionId) {
    return getHash(subscriberId.concat(sessionId));
  }

  private long getHash(String string) {
    return Hashing.murmur3_128().hashString(string, StandardCharsets.UTF_8).asLong();
  }

  private record Combo(Subscriber subscriber, SubscriberFilter subscriberFilter, Filters filters) {
  }
}
