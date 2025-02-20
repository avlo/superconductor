package com.prosilion.superconductor.service.request;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.hash.Hashing;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import com.prosilion.superconductor.service.request.pubsub.TerminatedSocket;
import com.prosilion.superconductor.util.EmptyFiltersException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.filter.Filters;
import nostr.event.filter.SinceFilter;
import nostr.event.filter.UntilFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
  public Long save(@NonNull Subscriber subscriber, @NonNull List<Filters> filtersList) throws EmptyFiltersException {
    removeSubscriberBySessionId(subscriber.getSessionId());
    long subscriberSessionHash = getHash(subscriber);
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
    return subscriberSessionHashComboMap.get(subscriberSessionHash).stream().findFirst().get().getSubscriber();
  }

  @Override
  public List<Filters> getFiltersList(@NonNull Long subscriberSessionHash) {
    return subscriberSessionHashComboMap.get(subscriberSessionHash).stream().map(Combo::getFilters).toList();
  }

  @EventListener
  public void terminateSocket(@NonNull TerminatedSocket terminatedSocket) {
    removeSubscriberBySessionId(terminatedSocket.sessionId());
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

  private void put(Subscriber subscriber, Filters filters) throws EmptyFiltersException {
    biMap.forcePut(subscriber.getSubscriberId(), subscriber.getSessionId());
    long subscriberSessionHash = getHash(subscriber);

    SubscriberFilter subscriberFilter = new SubscriberFilter();
    subscriberFilter.setSubscriberId(subscriberSessionHash);
    subscriberFilter.setLimit(filters.getLimit());

    setBoundCriterion(filters, SinceFilter.filterKey, subscriberFilter::setSince);
    setBoundCriterion(filters, UntilFilter.filterKey, subscriberFilter::setUntil);

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
  private static void setBoundCriterion(Filters filters, String bound, Consumer<Long> longConsumer) {
    Optional
        .ofNullable(
            filters.getFilterByType(bound))
        .<Long>map(filterable ->
            filterable.getFirst().getFilterCriterion())
        .ifPresent(longConsumer);
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

    public Combo(Subscriber subscriber, SubscriberFilter subscriberFilter, Filters filters) throws EmptyFiltersException {
      this.subscriber = subscriber;
      if (!checkMinimallyPopulatedFilters(subscriberFilter, filters))
        throw new EmptyFiltersException(String.format("invalid: empty filters encountered for subscriber [%s]", subscriber.getSubscriberId()));
      this.subscriberFilter = subscriberFilter;
      this.filters = filters;
    }

    //    TODO: confirm below not necessary since should be part of framework
    private static boolean checkMinimallyPopulatedFilters(SubscriberFilter subscriberFilter, Filters filters) {
      return true;
//      return Objects.nonNull(subscriberFilter.getSince())
//          || Objects.nonNull(subscriberFilter.getUntil())
//          || Objects.nonNull(subscriberFilter.getLimit())
//          || hasValidField(filters.getEvents(), getEventPredicate())
//          || hasValidField(filters.getAuthors(), getPubKeyPredicate())
//          || hasValidField(filters.getKinds(), getKindPredicate())
//          || hasValidField(filters.getReferencedEvents(), getEventPredicate())
//          || hasValidField(filters.getReferencePubKeys(), getPubKeyPredicate())
//          || hasValidGenericTagQuery(filters.getGenericTagQuery());
    }

    private static <T> boolean hasValidField(List<T> filtersField, Predicate<T> fieldPredicate) {
      return Objects.nonNull(filtersField)
          && !filtersField.isEmpty()
          && filtersField.stream().anyMatch(fieldPredicate);
    }

    private static Predicate<PublicKey> getPubKeyPredicate() {
      return pubKey -> !pubKey.toString().isBlank();
    }

    private static Predicate<GenericEvent> getEventPredicate() {
      return event -> !event.getId().isBlank();
    }

    Function<Kind, Boolean> kindBooleanFunction = kind -> !kind.toString().isBlank();

    private Predicate<Kind> getKindPredicate() {
      return kind -> {
        Function<Kind, Boolean> kindBooleanFunction = kind1 -> !kind1.toString().isBlank();
        return kindBooleanFunction.apply(kind);
      };
    }

    private static boolean hasValidGenericTagQuery(Map<String, List<String>> genericTagQuery) {
      return Objects.nonNull(genericTagQuery)
          && genericTagQuery.entrySet().stream().allMatch(entry -> Objects.nonNull(entry.getValue()))
          && genericTagQuery.entrySet().stream().noneMatch(entry -> entry.getValue().toString().isBlank());
    }
  }
}
