package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.dto.ClassifiedListingDto;
import com.prosilion.nostrrelay.dto.PriceTagDto;
import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import com.prosilion.nostrrelay.repository.ClassifiedListingEntityRepository;
import com.prosilion.nostrrelay.service.event.join.ClassifiedListingEntityEventEntityService;
import jakarta.persistence.NoResultException;
import lombok.extern.java.Log;
import nostr.base.ElementAttribute;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Service
public class ClassifiedListingEventService<T extends EventMessage> implements EventServiceIF<T> {
	private final ClassifiedListingEntityRepository classifiedListingEntityRepository;
	private final ClassifiedListingEntityEventEntityService joinService;
	private final PriceTagEntityService priceTagEntityService;
	private final EventService<ClassifiedListingEvent> eventService;

	public ClassifiedListingEventService(
			EventService<ClassifiedListingEvent> eventService,
			ClassifiedListingEntityRepository classifiedListingEntityRepository,
			ClassifiedListingEntityEventEntityService joinService,
			PriceTagEntityService priceTagEntityService) {
		this.eventService = eventService;
		this.classifiedListingEntityRepository = classifiedListingEntityRepository;
		this.joinService = joinService;
		this.priceTagEntityService = priceTagEntityService;
	}

	@Override
	public void processIncoming(T eventMessage) {
		log.log(Level.INFO, "processing incoming CLASSIFIED_LISTING: [{0}]", eventMessage);
		GenericEvent event = (GenericEvent) eventMessage.getEvent();
		Long savedEventId = eventService.saveEventEntity(event);

		Result priceTagDtoResult = createPriceTagDto(event);
		ClassifiedListingDto classifiedListingDto = getClassifiedListingDto(event, priceTagDtoResult);
		ClassifiedListingEntity classifiedListingEntity = saveClassifiedListing(classifiedListingDto);

		joinService.save(savedEventId, classifiedListingEntity.getId());
		priceTagEntityService.savePriceTag(savedEventId, classifiedListingDto.getPriceTag());

		ClassifiedListingEvent classifiedListingEvent = new ClassifiedListingEvent(
				event.getPubKey(),
				event.getTags(),
				event.getContent(),
				classifiedListingDto.convertDtoToEntity().convertEntityToDto()
		);
		classifiedListingEvent.setId(event.getId());
		eventService.publishEvent(savedEventId, classifiedListingEvent);
	}

	//  TODO: refactor below
	@NotNull
	private static ClassifiedListingDto getClassifiedListingDto(GenericEvent event, Result priceTagDtoResult) {
		ClassifiedListingDto classifiedListingDto = new ClassifiedListingDto(
				getReturnVal(priceTagDtoResult.genericTagsOnly(), "title"),
				getReturnVal(priceTagDtoResult.genericTagsOnly(), "summary"),
				PriceTagDto.createPriceTagDtoFromAttributes(priceTagDtoResult.priceTagDto().stream().findFirst().orElseThrow())
		);
		classifiedListingDto.setLocation(getReturnVal(priceTagDtoResult.genericTagsOnly(), "location"));
		classifiedListingDto.setPublishedAt(event.getCreatedAt());
		return classifiedListingDto;
	}

	//  TODO: refactor below method
	@NotNull
	private static Result createPriceTagDto(GenericEvent event) {
		List<GenericTag> genericTagsOnly = event.getTags().stream().map(baseTag -> (GenericTag) baseTag).toList();

		List<List<ElementAttribute>> priceTagDto = genericTagsOnly.stream()
				.filter(tag -> tag.getCode().equalsIgnoreCase("price")).map(GenericTag::getAttributes).toList();
		return new Result(genericTagsOnly, priceTagDto);
	}

	private ClassifiedListingEntity saveClassifiedListing(ClassifiedListingDto classifiedListingDto) {
		return Optional.of(classifiedListingEntityRepository.save(classifiedListingDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
	}

	private static String getReturnVal(List<GenericTag> genericTags, String val) {
		List<ElementAttribute> atts = genericTags.stream().filter(tag -> tag.getCode().equals(val)).findFirst().get().getAttributes();
		return (String) atts.stream().map(ea -> ea.getValue()).findFirst().get();
	}

	public ClassifiedListingEntity findById(Long id) {
		return classifiedListingEntityRepository.findById(id).get();
	}

	private record Result(List<GenericTag> genericTagsOnly, List<List<ElementAttribute>> priceTagDto) {
	}
}
