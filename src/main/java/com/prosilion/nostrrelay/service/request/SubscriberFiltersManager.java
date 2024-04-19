package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.entity.join.*;
import com.prosilion.nostrrelay.repository.join.*;
import jakarta.persistence.NoResultException;
import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
import org.springframework.stereotype.Service;

@Service
public class SubscriberFiltersManager {
	private final SubscriberFilterRepository subscriberFilterRepository;
	private final SubscriberFilterEventRepository subscriberFilterEventRepository;
	private final SubscriberFilterAuthorRepository subscriberFilterAuthorRepository;
	private final SubscriberFilterKindRepository subscriberFilterKindRepository;
	private final SubscriberFilterReferencedEventRepository subscriberFilterReferencedEventRepository;
	private final SubscriberFilterReferencedPubkeyRepository subscriberFilterReferencedPubkeyRepository;

	public SubscriberFiltersManager(
			SubscriberFilterRepository subscriberFilterRepository,
			SubscriberFilterEventRepository subscriberFilterEventRepository,
			SubscriberFilterAuthorRepository subscriberFilterAuthorRepository,
			SubscriberFilterKindRepository subscriberFilterKindRepository,
			SubscriberFilterReferencedEventRepository subscriberFilterReferencedEventRepository,
			SubscriberFilterReferencedPubkeyRepository subscriberFilterReferencedPubkeyRepository) {
		this.subscriberFilterRepository = subscriberFilterRepository;
		this.subscriberFilterEventRepository = subscriberFilterEventRepository;
		this.subscriberFilterAuthorRepository = subscriberFilterAuthorRepository;
		this.subscriberFilterKindRepository = subscriberFilterKindRepository;
		this.subscriberFilterReferencedEventRepository = subscriberFilterReferencedEventRepository;
		this.subscriberFilterReferencedPubkeyRepository = subscriberFilterReferencedPubkeyRepository;
	}

	public void saveFilters(Long subscriberId, FiltersList filtersList) {
		for (Filters filters : filtersList.getList()) {
			Long filterId = saveSubscriberFilter(subscriberId, filters);
			//      TODO: all below can be parallelized
			saveEvents(filterId, filters.getEvents());
			saveAuthors(filterId, filters.getAuthors());
			saveKinds(filterId, filters.getKinds());
			saveReferencedEvents(filterId, filters.getReferencedEvents());
			saveReferencedPubkeys(filterId, filters.getReferencePubKeys());
			//      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
		}
	}

	public void updateFilters(Long subscriberId, FiltersList filtersList) {
		for (Filters filters : filtersList.getList()) {
			Long filterId = saveSubscriberFilter(subscriberId, filters);
			//      TODO: all below can be parallelized
			saveEvents(filterId, filters.getEvents());
			saveAuthors(filterId, filters.getAuthors());
			saveKinds(filterId, filters.getKinds());
			saveReferencedEvents(filterId, filters.getReferencedEvents());
			saveReferencedPubkeys(filterId, filters.getReferencePubKeys());
			//      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
		}
	}

	private Long saveSubscriberFilter(Long subscriberId, Filters filters) {
		return subscriberFilterRepository.save(
				new SubscriberFilter(subscriberId, filters.getSince(), filters.getUntil(), filters.getLimit())).getId();
	}

	private void saveEvents(Long filterId, EventList eventList) {
		eventList.getList().iterator().forEachRemaining(event ->
				subscriberFilterEventRepository.save(
						new SubscriberFilterEvent(filterId, event.getId())));
	}

	private void saveAuthors(Long filterId, PublicKeyList authorList) {
		authorList.getList().iterator().forEachRemaining(author ->
				subscriberFilterAuthorRepository.save(
						new SubscriberFilterAuthor(filterId, author.toString())));
	}

	private void saveKinds(Long filterId, KindList kindList) {
		kindList.getList().iterator().forEachRemaining(kind ->
				subscriberFilterKindRepository.save(
						new SubscriberFilterKind(filterId, kind)));
	}

	private void saveReferencedEvents(Long filterId, EventList eventList) {
		eventList.getList().iterator().forEachRemaining(event ->
				subscriberFilterReferencedEventRepository.save(
						new SubscriberFilterReferencedEvent(filterId, event.getId())));
	}

	private void saveReferencedPubkeys(Long filterId, PublicKeyList publicKeyList) {
		publicKeyList.getList().iterator().forEachRemaining(pubkey ->
				subscriberFilterReferencedPubkeyRepository.save(
						new SubscriberFilterReferencedPubkey(filterId, pubkey.toString())));
	}

	/**
	 * REMOVES
	 */

	public void removeAllFilters(Long subscriberId) {
		Long filterId = removeSubscriberFilter(subscriberId);
		//      TODO: all below can be parallelized
		removeEvents(filterId);
		removeAuthors(filterId);
		removeKinds(filterId);
		removeReferencedEvents(filterId);
		removeReferencedPubkeys(filterId);
		//      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
	}

	private Long removeSubscriberFilter(Long subscriberId) {
		return subscriberFilterRepository.findById(subscriberId).orElseThrow(NoResultException::new).getId();
	}

	private void removeEvents(Long filterId) {
		subscriberFilterEventRepository.deleteById(filterId);
	}

	private void removeAuthors(Long filterId) {
		subscriberFilterAuthorRepository.deleteById(filterId);
	}

	private void removeKinds(Long filterId) {
		subscriberFilterKindRepository.deleteById(filterId);
	}

	private void removeReferencedEvents(Long filterId) {
		subscriberFilterReferencedEventRepository.deleteById(filterId);
	}

	private void removeReferencedPubkeys(Long filterId) {
		subscriberFilterReferencedPubkeyRepository.deleteById(filterId);
	}
}
