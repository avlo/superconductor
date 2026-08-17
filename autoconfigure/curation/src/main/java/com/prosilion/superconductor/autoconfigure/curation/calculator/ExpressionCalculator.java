package com.prosilion.superconductor.autoconfigure.curation.calculator;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public class ExpressionCalculator {
  private static final String VALID_MATH_OPERATORS_REGEX = "[+\\-*/]";
  private static final Pattern pattern = Pattern.compile(VALID_MATH_OPERATORS_REGEX);
  private static final String accumulator = "accumulator";

  public static String calculate(@NonNull String currentTotal, @NonNull String inputOperationWithOperand) {
    String stripped = inputOperationWithOperand.strip();
    Matcher matcher = pattern.matcher(stripped);
    if (!matcher.find())
      throw new NostrException();

    String operator = matcher.group().trim();
    assert (operator.length() == 1);

    try {
      return new Expression(
          String.format("%s %s %s", accumulator, operator, stripped.replace(operator, "").strip()))
          /**
           * first/String parameter in call to .with(...):
           *    {@link Expression#with(String, Object)}
           * acts as an accumulator
           */
          .with(
              accumulator,
              new BigDecimal(currentTotal.trim()))
          .evaluate().getNumberValue().toPlainString();
    } catch (EvaluationException | ParseException e) {
      throw new NostrException("", e);
    }
  }
}
