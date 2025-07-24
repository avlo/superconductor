package com.prosilion.superconductor.sqlite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.prosilion.superconductor.sqlite.util.OrderAgnosticJsonComparator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonEquivalencyTest {
  private final ObjectMapper MAPPER_AFTERBURNER = JsonMapper.builder().addModule(new AfterburnerModule()).build();

  public final String referenceJson;
  public final String reorderedJson;
  public final String failTargetJson;
  public final String failKindTargetJson;

  public JsonEquivalencyTest() throws IOException {
    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_equiv_input.txt"))) {
      this.referenceJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_un_equiv_input.txt"))) {
      this.failTargetJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_equiv_reordered.txt"))) {
      this.reorderedJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/json_un_equiv_created_at_input.txt"))) {
      this.failKindTargetJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @Test
  void testEquivalency() throws JsonProcessingException {
    assertTrue(OrderAgnosticJsonComparator.equalsJson(
        MAPPER_AFTERBURNER.readTree(referenceJson),
        MAPPER_AFTERBURNER.readTree(reorderedJson)));
  }

  @Test
  void testNonEquivalency() throws JsonProcessingException {
    assertFalse(OrderAgnosticJsonComparator.equalsJson(
        MAPPER_AFTERBURNER.readTree(referenceJson),
//    below catches single-char diff in "content" tag (2111111111 -vs- 1111111111)
        MAPPER_AFTERBURNER.readTree(failTargetJson)));
  }

  @Test
  void testNonEquivalencyKind() throws JsonProcessingException {
//    below catches single-char diff in "created_at" tag (1717357053050 -vs- 1717357053051)
    assertFalse(OrderAgnosticJsonComparator.equalsJson(
        MAPPER_AFTERBURNER.readTree(referenceJson),
        MAPPER_AFTERBURNER.readTree(failKindTargetJson)));
  }
}
