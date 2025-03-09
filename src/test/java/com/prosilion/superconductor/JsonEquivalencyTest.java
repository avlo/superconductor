package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosilion.superconductor.util.OrderAgnosticJsonComparator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonEquivalencyTest {
  private final ObjectMapper mapper = new ObjectMapper();

  public final String tempTargetJson;
  public final String targetJson;
  public final String failTargetJson;

  public JsonEquivalencyTest() throws IOException {
    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_equiv_input.txt"))) {
      this.tempTargetJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_un_equiv_input.txt"))) {
      this.failTargetJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_equiv_reordered.txt"))) {
      this.targetJson = lines.collect(Collectors.joining("\n"));
    }

  }

  @Test
  void testEquivalency() throws JsonProcessingException {
    JsonNode expected = mapper.readTree(targetJson);
    JsonNode actual = mapper.readTree(tempTargetJson);
    assertTrue(OrderAgnosticJsonComparator.equalsJson(expected, actual));
  }

  @Test
  void testNonEquivalency() throws JsonProcessingException {
    JsonNode expected = mapper.readTree(failTargetJson);
    JsonNode actual = mapper.readTree(tempTargetJson);
//    below catches single-char diff in "content" tag (2111111111 -vs- 1111111111)
    assertFalse(OrderAgnosticJsonComparator.equalsJson(expected, actual));
  }
}
