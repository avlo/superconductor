package com.prosilion.superconductor.base.dto;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class BadgeDefinitionReputationEventVariantDto extends BadgeDefinitionAwardEventDto {
  private final List<FormulaEventDtoVariantWithFormulaEvent> formulaEventDtos;

  public BadgeDefinitionReputationEventVariantDto(
      @NonNull Identity identity,
      @NonNull IdentifierTag identifierTag,
      @NonNull FormulaEventDtoVariantWithFormulaEvent formulaEventDto) throws NostrException {
    this(
        identity,
        identifierTag,
        List.of(formulaEventDto));
  }

  public BadgeDefinitionReputationEventVariantDto(
      @NonNull Identity identity,
      @NonNull IdentifierTag identifierTag,
      @NonNull List<FormulaEventDtoVariantWithFormulaEvent> formulaEventDtos) throws NostrException {
    super(
        new BadgeDefinitionReputationEvent(
            identity,
            identifierTag,
            formulaEventDtos.stream()
                .map(FormulaEventDtoVariantWithFormulaEvent::getFormulaEvent)
                .map(FormulaEvent::getId)
                .map(EventTag::new).toList(),
            List.of(),
            defaultContentFromFormulaOperators(identifierTag, formulaEventDtos)));
    this.formulaEventDtos = formulaEventDtos;
  }

  public BadgeDefinitionReputationEvent getBadgeDefinitionAwardEvent() {
    return (BadgeDefinitionReputationEvent) super.getBadgeDefinitionAwardEvent();
  }
  
  private static String defaultContentFromFormulaOperators(
      IdentifierTag identifierTagReputationDefinition,
      List<FormulaEventDtoVariantWithFormulaEvent> formulaEvents) {
    List<IdentifierTag> identifierTags = formulaEvents.stream().map(FormulaEventDtoVariantWithFormulaEvent::getIdentifierTag).toList();

    Optional.of(
            identifierTags.stream().distinct().count() != formulaEvents.size())
        .filter(Boolean::booleanValue)
        .map(bool -> {
          throw new NostrException(String.format("Matching formula events found in %s", identifierTags));
        });

    return String.format("%s == (previous)%s%s", identifierTagReputationDefinition.getUuid(), identifierTagReputationDefinition.getUuid(), operatorFormatDisplayIterator(formulaEvents));
  }

  private static String operatorFormatDisplayIterator(List<FormulaEventDtoVariantWithFormulaEvent> formulaEvents) {
    StringBuilder sb = new StringBuilder();
    formulaEvents.forEach(formulaEvent -> sb
        .append(" ")
        .append(formulaEvent.getFormula())
        .append("(")
        .append(
            formulaEvent.getIdentifierTag()
                .getUuid())
        .append(")"));
    return sb.toString();
  }
}
