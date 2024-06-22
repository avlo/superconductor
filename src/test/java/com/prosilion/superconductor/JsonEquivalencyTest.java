package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class JsonEquivalencyTest {
  private static final String TARGET_TEXT_MESSAGE_EVENT_CONTENT = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";

  private final ObjectMapper mapper = new ObjectMapper();

  @SuppressWarnings("preview")
  public final static String tempTargetJson =
      StringTemplate.STR."""
          [
            "EVENT",
              {
                "content":"1111111111",
                "id":"\{TARGET_TEXT_MESSAGE_EVENT_CONTENT}",
                "kind":1,
                "created_at":1717357053050,
                "pubkey":"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984",
                "tags": [
                  [
                    "p",
                    "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984"
                  ],
                  [
                    "e",
                    "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"
                  ],
                  [
                    "g",
                    "textnote geo-tag-1"
                  ]
                ],
              "sig":"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546"
            }
          ]
      """;

  @SuppressWarnings("preview")
  public final static String targetJson =
      StringTemplate.STR."""
          [
            "EVENT",
              {
                "content":"1111111111",
                "id":"\{TARGET_TEXT_MESSAGE_EVENT_CONTENT}",
                "kind":1,
                "created_at":1717357053050,
                "pubkey":"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984",
                "tags": [
                  [
                    "e",
                    "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"
                  ],
                  [
                    "p",
                    "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984"
                  ],
                  [
                    "g",
                    "textnote geo-tag-1"
                  ]
                ],
              "sig":"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546"
            }
          ]
      """;

  @Test
  void testEquivalency() throws JsonProcessingException {
    JsonNode expected = mapper.readTree(targetJson);
    JsonNode actual = mapper.readTree(tempTargetJson);
    ComparatorWithoutOrder.equalsJson(expected, actual);
  }
}

