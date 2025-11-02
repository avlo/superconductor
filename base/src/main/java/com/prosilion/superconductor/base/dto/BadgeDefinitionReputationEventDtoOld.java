//package com.prosilion.superconductor.base.dto;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.filter.Filterable;
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import lombok.Getter;
//import org.springframework.lang.NonNull;
//
//@Getter
//public class BadgeDefinitionReputationEventDtoOld extends BadgeDefinitionAwardEventDto {
//  private final List<FormulaEvent> formulaEvents;
//
//  public BadgeDefinitionReputationEventDtoOld(
//      @NonNull Identity identity,
//      @NonNull IdentifierTag identifierTag,
//      @NonNull FormulaEvent formulaEvent) throws NostrException {
//    this(identity, identifierTag, Collections.singletonList(formulaEvent));
//  }
//
//  public BadgeDefinitionReputationEventDtoOld(
//      @NonNull Identity identity,
//      @NonNull IdentifierTag identifierTag,
//      @NonNull List<FormulaEvent> formulaEvents) throws NostrException {
//    this(
//        identity,
//        identifierTag,
//        formulaEvents,
//        List.of(),
//        defaultContentFromFormulaOperators(identifierTag, formulaEvents));
//  }
//
//  private static String defaultContentFromFormulaOperators(
//      IdentifierTag identifierTagReputationDefinition,
//      List<FormulaEvent> formulaEvents) {
//    List<IdentifierTag> identifierTags = getIdentifierTags(Collections.unmodifiableList(formulaEvents));
//
//    Optional.of(
//            identifierTags.stream().distinct().count() != formulaEvents.size())
//        .filter(Boolean::booleanValue)
//        .map(bool -> {
//          throw new NostrException(String.format("Matching formula events found in %s", identifierTags));
//        });
//
//    return String.format("%s == (previous)%s%s", identifierTagReputationDefinition.getUuid(), identifierTagReputationDefinition.getUuid(), operatorFormatDisplayIterator(formulaEvents));
//  }
//
//  private static String operatorFormatDisplayIterator(List<FormulaEvent> formulaEvents) {
//    StringBuilder sb = new StringBuilder();
//    formulaEvents.forEach(formulaEvent -> sb
//        .append(" ")
//        .append(formulaEvent.getFormula())
//        .append("(")
//        .append(
//            getIdentifierTag(formulaEvent)
//                .getUuid())
//        .append(")"));
//    return sb.toString();
//  }
//
//  public BadgeDefinitionReputationEventDtoOld(
//      @NonNull Identity identity,
//      @NonNull IdentifierTag identifierTag,
//      @NonNull List<FormulaEvent> formulaEvents,
//      @NonNull List<BaseTag> baseTags,
//      @NonNull String content) throws NostrException {
//    super(
//        new BadgeDefinitionReputationEvent(
//            identity,
//            identifierTag,
//            formulaEvents.stream()
//                .map(FormulaEvent::getId)
//                .map(EventTag::new).toList(),
//            baseTags,
//            content));
//    this.formulaEvents = formulaEvents;
//  }
//
//  @JsonIgnore
//  public List<EventTag> getEventTags() {
//    return formulaEvents.stream()
//        .map(FormulaEvent::getId)
//        .map(EventTag::new).toList();
//  }
//
//  protected static List<IdentifierTag> getIdentifierTags(List<EventIF> formulaEvents) {
//    return formulaEvents.stream().map(BadgeDefinitionReputationEventDtoOld::getIdentifierTag).toList();
//  }
//
//  protected static IdentifierTag getIdentifierTag(EventIF formulaEvent) {
//    return Filterable.getTypeSpecificTagsStream(IdentifierTag.class, formulaEvent).findFirst().orElseThrow();
//  }
//}
