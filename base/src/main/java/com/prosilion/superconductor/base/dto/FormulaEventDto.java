//package com.prosilion.superconductor.base.dto;
//
//import com.ezylang.evalex.Expression;
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.tag.IdentifierTag;
//import java.util.Objects;
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.lang.NonNull;
//
//@Getter
//public class FormulaEventDto extends ArbitraryCustomAppDataEventDto {
//  private final BadgeDefinitionAwardEventDto badgeDefinitionAwardEventDto;
//
//  public FormulaEventDto(@NonNull BadgeDefinitionAwardEventDto badgeDefinitionAwardEventDto) throws NostrException, ParseException {
//    super(
//        badgeDefinitionAwardEventDto.getIdentifierTag(),
//        validate(badgeDefinitionAwardEventDto.getBadgeDefinitionAwardEvent().getContent()));
//    this.badgeDefinitionAwardEventDto = badgeDefinitionAwardEventDto;
//  }
//
//  @Override
//  public IdentifierTag getIdentifierTag() {
//    return super.getIdentifierTag();
//  }
//
//  public String getFormula() {
//    return super.getContent();
//  }
//
//  private static String validate(String formula) throws ParseException {
//    if (StringUtils.isBlank(formula))
//      throw new ParseException(formula, "supplied formula is blank");
//    new Expression(
//        String.format("%s %s", "validate", formula)).validate();
//    return formula;
//  }
//
//  @Override
//  public boolean equals(Object obj) {
//    if (!obj.getClass().isAssignableFrom(FormulaEventDto.class))
//      return false;
//
//    FormulaEventDto that = (FormulaEventDto) obj;
//    return
//        super.equals(that) 
//            &&
//        Objects.equals(
//          this.badgeDefinitionAwardEventDto,
//          that.badgeDefinitionAwardEventDto);
//  }
//}
