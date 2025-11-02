package com.prosilion.superconductor.base.dto;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import java.util.Objects;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class FormulaEventDtoVariantWithFormulaEvent extends ArbitraryCustomAppDataEventDto {
  private final FormulaEvent formulaEvent;

  public FormulaEventDtoVariantWithFormulaEvent(@NonNull FormulaEvent formulaEvent) throws NostrException {
    super(
        formulaEvent.getTypeSpecificTags(IdentifierTag.class).getFirst(),
        formulaEvent.getContent());
    this.formulaEvent = formulaEvent;
  }

  @Override
  public IdentifierTag getIdentifierTag() {
    return super.getIdentifierTag();
  }

  public String getFormula() {
    return super.getContent();
  }

  @Override
  public boolean equals(Object obj) {
    if (!obj.getClass().isAssignableFrom(FormulaEventDtoVariantWithFormulaEvent.class))
      return false;

    FormulaEventDtoVariantWithFormulaEvent that = (FormulaEventDtoVariantWithFormulaEvent) obj;
    return
        super.equals(that) 
            &&
        Objects.equals(
          this.formulaEvent,
          that.formulaEvent);
  }
}
