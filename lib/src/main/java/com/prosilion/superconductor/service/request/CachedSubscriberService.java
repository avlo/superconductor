package com.prosilion.superconductor.service.request;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.hash.Hashing;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.SinceFilter;
import com.prosilion.nostr.filter.event.UntilFilter;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import com.prosilion.superconductor.service.request.pubsub.TerminatedSocket;
import jakarta.validation.constraints.NotEmpty;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

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
  public Long save(@NonNull Subscriber subscriber, @NotEmpty List<Filters> filtersList) {
    Preconditions.checkArgument(!filtersList.isEmpty());
    long subscriberSessionHash = getHash(subscriber);

//    TODO: below quick impl, needs cleanup
    if (checkIdenticalFilters(subscriberSessionHash, filtersList))
      return subscriberSessionHash;

    removeSubscriberBySessionId(subscriber.getSessionId());
    subscriber.setSubscriberSessionHash(subscriberSessionHash);
    for (Filters filters : filtersList) {
      put(subscriber, filters);
    }
    return subscriberSessionHash;
  }

  //  TODO: list of long???  why not just long
  @Override
  public Map<Long, List<Filters>> getAllFiltersOfAllSubscribers() {
    Map<Long, List<Filters>> map = new HashMap<>();
    subscriberSessionHashComboMap.forEach((key, value) -> map.put(key, value.stream().map(Combo::getFilters).toList()));
    return map;
  }

  //  @Cacheable("subscriber")
  @Override
  public Subscriber get(@NonNull Long subscriberSessionHash) {
    return subscriberSessionHashComboMap.get(subscriberSessionHash).stream().findFirst().orElseThrow().getSubscriber();
  }

  @Override
  public List<Filters> getFiltersList(@NonNull Long subscriberSessionHash) {
    return subscriberSessionHashComboMap.get(subscriberSessionHash).stream().map(Combo::getFilters).toList();
  }

  @EventListener
  public void terminateSocket(@NonNull TerminatedSocket terminatedSocket) {
    removeSubscriberBySessionId(terminatedSocket.sessionId());
  }

  public boolean checkIdenticalFilters(@NonNull Long subscriberSessionHash, @NotEmpty List<Filters> filtersList) {
//    TODO: below quick impl, needs cleanup    
    if (!subscriberSessionHashComboMap.containsKey(subscriberSessionHash))
      return false;
    return subscriberSessionHashComboMap
        .get(subscriberSessionHash).stream().map(Combo::getFilters).toList().equals(filtersList);
  }

  @Override
  public List<Long> removeSubscriberBySessionId(@NonNull String sessionId) {
    String subscriberId = biMap.inverse().getOrDefault(sessionId, "");
    long hash = getHash(
        new Subscriber(
            subscriberId,
            sessionId,
            true));
    biMap.inverse().remove(sessionId);
    subscriberSessionHashComboMap.remove(hash);
    return List.of(hash);
  }

  @Override
  public Long removeSubscriberBySubscriberId(@NonNull String subscriberId) {
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

    SubscriberFilter subscriberFilter = new SubscriberFilter();
    subscriberFilter.setSubscriberId(subscriberSessionHash);
    subscriberFilter.setLimit(filters.getLimit());

    setBoundCriterion(filters, SinceFilter.FILTER_KEY, subscriberFilter::setSince, Long.MIN_VALUE);
    setBoundCriterion(filters, UntilFilter.FILTER_KEY, subscriberFilter::setUntil, Long.MAX_VALUE);

    Combo combo = new Combo(
        subscriber,
        subscriberFilter,
        filters);

    if (!subscriberSessionHashComboMap.containsKey(subscriberSessionHash)) {
      subscriberSessionHashComboMap.put(subscriberSessionHash, List.of(combo));
      return;
    }
    subscriberSessionHashComboMap.get(subscriberSessionHash).add(combo);
  }

  private static void setBoundCriterion(Filters filters, String bound, Consumer<Long> longConsumer, Long epoch) {
    longConsumer.accept(
        filters.getFilterByType(bound).stream()
            .map(filterable ->
                (Long) filterable.getFilterable())
            .findFirst()
            .orElse(epoch));
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

  @Getter
  private static class Combo {
    private final Subscriber subscriber;
    private final SubscriberFilter subscriberFilter;
    private final Filters filters;

    public Combo(Subscriber subscriber, SubscriberFilter subscriberFilter, Filters filters) {
      this.subscriber = subscriber;
      this.subscriberFilter = subscriberFilter;
      this.filters = filters;
    }
  }
}
