package com.prosilion.superconductor.base.service.event.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.prosilion.nostr.enums.Kind;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Objects;

public interface KindTypeIF {
  Kind getKind();

  Kind getKindDefinition();

  String getName();

  KindTypeIF[] getValues();

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  default KindTypeIF valueOf(Kind kind, Kind kindDefinition, String name) {
    assert ValueRange.of(0, 65_535).isValidIntValue(kind.getValue()) :
        new IllegalArgumentException(String.format("Kind must be between 0 and 65535 but was [%d]",
            kind.getValue()));

    assert Objects.equals(kindDefinition, Kind.BADGE_DEFINITION_EVENT) :
        new IllegalArgumentException(
            String.format("Kind definition must be [%d], but was [%d]",
                Kind.BADGE_DEFINITION_EVENT.getValue(),
                kind.getValue()));

    for (KindTypeIF k : getValues()) {
      if (k.getKind().equals(kind) && k.getKindDefinition().equals(kindDefinition) && k.getName().equals(name))
        return k;
    }

    throw new IllegalArgumentException(
        String.format("Kind [%s], KindDefinition [%s], KindName [%s] does not match any KindTypes in [%s]",
            kind,
            kindDefinition,
            name,
            Arrays.toString(getValues())));
  }

  default boolean equals(Kind k2, Kind k3, String name) {
    boolean kindEquals = getKind().equals(k2);
    boolean kindDefinitionEquals = getKindDefinition().equals(k3);
    boolean kindNameEquals = getName().equalsIgnoreCase(name);
    return (kindEquals && kindDefinitionEquals && kindNameEquals);
  }
}

