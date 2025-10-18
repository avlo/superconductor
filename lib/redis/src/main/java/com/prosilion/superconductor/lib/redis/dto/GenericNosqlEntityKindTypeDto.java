//package com.prosilion.superconductor.lib.redis.dto;
//
//import com.prosilion.nostr.enums.KindTypeIF;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.superconductor.base.service.event.service.GenericEventKind;
//import com.prosilion.superconductor.base.service.event.service.GenericEventKindType;
//import com.prosilion.superconductor.base.service.event.service.GenericEventKindTypeIF;
//import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
//import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
//
//public record GenericNosqlEntityKindTypeDto(BaseEvent baseEvent, KindTypeIF kindType) {
//
//  public EventNosqlEntityIF convertDtoToEntity() {
//    EventNosqlEntity entity = EventNosqlEntity.of(
//        baseEvent.getId(),
//        baseEvent.getKind().getValue(),
//        baseEvent.getPublicKey().toString(),
//        baseEvent.getCreatedAt(),
//        baseEvent.getContent(),
//        baseEvent.getSignature().toString());
//
//    entity.setTags(baseEvent.getTags());
//    return entity;
//  }
//
//  public GenericEventKindTypeIF convertBaseEventToGenericEventKindTypeIF() {
//    return new GenericEventKindType(
//        new GenericEventKind(
//            baseEvent.getId(),
//            baseEvent.getPublicKey(),
//            baseEvent.getCreatedAt(),
//            baseEvent.getKind(),
//            baseEvent.getTags(),
//            baseEvent.getContent(),
//            baseEvent.getSignature()),
//        kindType);
//  }
//}
