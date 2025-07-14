package com.prosilion.superconductor.noop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static com.prosilion.superconductor.TextNoteEventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {"/reqmessageit.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("testnoop")
class EventNoOpReqMessageIT {
  private final NostrRelayService nostrRelayService;

  private final String eventIdFromEventSql = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  private final String authorPubkeyFromEventSql = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

  @Autowired
  EventNoOpReqMessageIT(@NonNull NostrRelayService nostrRelayService) {
    this.nostrRelayService = nostrRelayService;
  }

  @Test
  void testReqFilteredByEventAndAuthor() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventIdFromEventSql));
    AuthorFilter authorFilter = new AuthorFilter(new PublicKey(authorPubkeyFromEventSql));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventIdFromEventSql)));
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().toHexString().equals(authorPubkeyFromEventSql)));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();
    EventFilter eventFilter = new EventFilter(new GenericEventId(eventIdFromEventSql));
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventIdFromEventSql)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter authorFilter = new AuthorFilter(new PublicKey(authorPubkeyFromEventSql));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().toHexString().equals(authorPubkeyFromEventSql)));
  }

  private String createAuthorReqJson(@NonNull String subscriberId, @NonNull String authorPubkey) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"" + authorPubkey + "\"]}]";
  }
}
