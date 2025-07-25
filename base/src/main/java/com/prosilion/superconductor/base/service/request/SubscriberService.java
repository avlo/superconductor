package com.prosilion.superconductor.base.service.request;

import com.prosilion.nostr.filter.Filters;
import com.prosilion.superconductor.base.Subscriber;
import com.prosilion.superconductor.base.util.EmptyFiltersException;
import com.prosilion.superconductor.base.util.NoExistingUserException;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public interface SubscriberService {
  Long save(@NonNull Subscriber subscriber, @NotEmpty List<Filters> filtersList) throws EmptyFiltersException;

  List<Long> removeSubscriberBySessionId(@NonNull String sessionId);

  Long removeSubscriberBySubscriberId(@NonNull String subscriberId) throws NoExistingUserException;

  List<Filters> getFiltersList(@NonNull Long subscriberId);

  Map<Long, List<Filters>> getAllFiltersOfAllSubscribers();

  Subscriber get(@NonNull Long subscriberHash);
}
