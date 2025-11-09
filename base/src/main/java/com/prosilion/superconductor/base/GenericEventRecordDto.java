//package com.prosilion.superconductor.base;
//
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.TextNoteEvent;
//import java.lang.reflect.Constructor;
//import java.util.Optional;
//import org.apache.commons.lang3.stream.Streams;
//
//public record GenericEventRecordDto(GenericEventRecord genericEventRecord) {
//  public Optional<? extends BaseEvent> convertGenericEventRecordToBaseEventRxR() {
//    Class<? extends BaseEvent> baseEventFromKind = createBaseEventFromKind(genericEventRecord.getKind());
//
//    Streams.FailableStream<? extends Constructor<? extends BaseEvent>> map = Streams.failableStream(baseEventFromKind)
//        .map(aClass ->
//            aClass.getConstructor(GenericEventRecord.class));
//
//    Streams.FailableStream<? extends BaseEvent> newInstance = map.map(Constructor::newInstance);
//
//    Optional<? extends BaseEvent> first = newInstance.stream().findFirst();
//
//    return first;
//  }
//
//  public <T extends BaseEvent> Optional<T> convertGenericEventRecordToBaseEvent() {
//    Class<T> baseEventFromKind = createBaseEventFromKindOG(genericEventRecord.getKind());
//
//    Constructor<T> constructor = null;
//    try {
//      constructor = baseEventFromKind.getConstructor(GenericEventRecord.class);
//    } catch (NoSuchMethodException e) {
//      throw new RuntimeException(e);
//    }
//
//    Optional<T> o = Streams.failableStream(constructor).map(ctor -> ctor.newInstance(genericEventRecord)).stream().findFirst();
//    return o;
//  }
//
//  private Class<? extends BaseEvent> createBaseEventFromKind(Kind kind) {
//    return switch (kind) {
//      case Kind.BADGE_AWARD_EVENT -> BadgeAwardAbstractEvent.class;
//      default -> TextNoteEvent.class;
//    };
//  }
//
//  private <T extends BaseEvent> Class<T> createBaseEventFromKindOG(Kind kind) {
//    return switch (kind) {
//      case Kind.BADGE_AWARD_EVENT -> (Class<T>) BadgeAwardAbstractEvent.class;
//      case Kind.ARBITRARY_CUSTOM_APP_DATA -> (Class<T>) FormulaEvent.class;
//      case Kind.BADGE_DEFINITION_EVENT -> (Class<T>) BadgeDefinitionAwardEvent.class;
//      default -> (Class<T>) TextNoteEvent.class;
//    };
//  }
//}
