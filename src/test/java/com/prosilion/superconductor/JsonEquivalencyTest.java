package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JsonEquivalencyTest {
  private final ObjectMapper mapper = new ObjectMapper();

  public final String tempTargetJson;
  public final String targetJson;

  public JsonEquivalencyTest() throws IOException {
    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_equiv_input.txt"))) {
      this.tempTargetJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_equiv_reordered.txt"))) {
      this.targetJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @Test
  void testEquivalency() throws JsonProcessingException {
    JsonNode expected = mapper.readTree(targetJson);
    JsonNode actual = mapper.readTree(tempTargetJson);
    ComparatorWithoutOrder.equalsJson(expected, actual);
  }
}
