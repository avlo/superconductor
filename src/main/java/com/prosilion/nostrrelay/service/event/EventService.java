package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.dto.EventDto;
import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.repository.EventEntityRepository;
import com.prosilion.nostrrelay.service.EventNotifierEngine;
import com.prosilion.nostrrelay.service.event.join.EventEntityTagEntityService;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log
@Getter
@Service
public class EventService<T extends EventMessage, U extends GenericEvent> {
	private final EventEntityTagEntityService eventEntityTagEntityService;
	private final EventEntityRepository eventEntityRepository;
	private final EventNotifierEngine<U> eventNotifierEngine;

	@Autowired
	public EventService(EventEntityTagEntityService eventEntityTagEntityService, EventEntityRepository eventEntityRepository, EventNotifierEngine<U> eventNotifierEngine) {
		this.eventEntityTagEntityService = eventEntityTagEntityService;
		this.eventEntityRepository = eventEntityRepository;
		this.eventNotifierEngine = eventNotifierEngine;
	}

	protected Long saveEventEntity(GenericEvent event) {
		List<BaseTag> baseTagsOnly = event.getTags().stream()
				.filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
				.toList();
		event.getTags().removeAll(baseTagsOnly);

		EventDto eventToSave = new EventDto(
				event.getPubKey(),
				event.getId(),
				Kind.valueOf(event.getKind()),
				event.getNip(),
				event.getCreatedAt(),
				event.getSignature(),
				baseTagsOnly,
				event.getContent()
		);

		EventEntity savedEntity = Optional.of(eventEntityRepository.save(eventToSave.convertDtoToEntity())).orElseThrow(NoResultException::new);
		return eventEntityTagEntityService.saveBaseTags(baseTagsOnly, savedEntity.getId());
	}

	protected void publishEvent(Long id, U event) {
		eventNotifierEngine.nostrEventHandler(new AddNostrEvent<U>(id, event, Kind.valueOf(event.getKind())));
	}
}
