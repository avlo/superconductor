package com.prosilion.superconductor.redis.config;

import com.fasterxml.jackson.annotation.JsonValue;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.type.KindTypeIF;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestKindType implements KindTypeIF {
  UNIT_UPVOTE(Kind.BADGE_AWARD_EVENT, Kind.BADGE_DEFINITION_EVENT, "UNIT_UPVOTE"),
  UNIT_DOWNVOTE(Kind.BADGE_AWARD_EVENT, Kind.BADGE_DEFINITION_EVENT, "UNIT_DOWNVOTE"),
  UNIT_REPUTATION(Kind.BADGE_AWARD_EVENT, Kind.BADGE_DEFINITION_EVENT, "UNIT_REPUTATION");

  private final Kind kind;
  private final Kind kindDefinition;

  @JsonValue
  private final String name;


  @Override
  public KindTypeIF[] getValues() {
    return TestKindType.values();
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
