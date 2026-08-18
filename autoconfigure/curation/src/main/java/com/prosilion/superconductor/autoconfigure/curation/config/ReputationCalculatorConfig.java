package com.prosilion.superconductor.autoconfigure.curation.config;

import com.prosilion.superconductor.autoconfigure.base.condition.EventCurationActiveCondition;
import com.prosilion.superconductor.autoconfigure.curation.InvalidReputationCalculatorException;
import com.prosilion.superconductor.autoconfigure.curation.calculator.ReputationCalculatorIF;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Conditional;

@AutoConfiguration
@Conditional(EventCurationActiveCondition.class)
public class ReputationCalculatorConfig {
  ReputationCalculatorIF reputationCalculatorIF(
     @NonNull @Value("${reputation.calculator.impl}") String calculator,
     @NonNull List<ReputationCalculatorIF> calculatorIFS) {
    return Optional.ofNullable(
          calculatorIFS.stream().collect(
                Collectors.toMap(
                   ReputationCalculatorIF::getFullyQualifiedCalculatorName,
                   Function.identity(),
                   (prev, next) -> next, HashMap::new))
             .get(calculator))
       .orElseThrow(() ->
          new InvalidReputationCalculatorException(calculator, calculatorIFS.stream().map(ReputationCalculatorIF::getFullyQualifiedCalculatorName).collect(Collectors.toList())));
  }
}

