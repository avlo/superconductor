package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.util.EmptyFiltersException;
import com.prosilion.superconductor.util.NoExistingUserException;
import lombok.NonNull;
import nostr.event.filter.FiltersCore;

import java.util.List;
import java.util.Map;

public interface SubscriberService {
  Long save(@NonNull Subscriber subscriber, @NonNull List<FiltersCore> filtersList) throws EmptyFiltersException;

  List<Long> removeSubscriberBySessionId(@NonNull String sessionId);

  Long removeSubscriberBySubscriberId(@NonNull String subscriberId) throws NoExistingUserException;

  List<FiltersCore> getFiltersList(@NonNull Long subscriberId);

  Map<Long, List<FiltersCore>> getAllFiltersOfAllSubscribers();

  Subscriber get(@NonNull Long subscriberHash);
}
