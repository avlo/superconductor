package com.prosilion.superconductor.service.event.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuperconductorKindType implements KindTypeIF {
  UPVOTE(Kind.BADGE_AWARD_EVENT, Kind.BADGE_DEFINITION_EVENT, "UPVOTE"),
  DOWNVOTE(Kind.BADGE_AWARD_EVENT, Kind.BADGE_DEFINITION_EVENT, "DOWNVOTE");

  private final Kind kind;
  private final Kind kindDefinition;

  @JsonValue
  private final String name;


  @Override
  public KindTypeIF[] getValues() {
    return SuperconductorKindType.values();
  }

  @Generated
  public Kind getKind() {
    return this.kind;
  }

  @Generated
  public Kind getKindDefinition() {
    return this.kindDefinition;
  }

  @Generated
  public String getName() {
    return this.name;
  }
}
