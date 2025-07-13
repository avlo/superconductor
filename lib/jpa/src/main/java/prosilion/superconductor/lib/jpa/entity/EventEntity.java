package prosilion.superconductor.lib.jpa.entity;

import com.prosilion.nostr.crypto.NostrUtil;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.base.EventEntityIF;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity implements EventEntityIF {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true)
  private String eventIdString;
  private String pubKey;
  private Integer kind;
  private Long createdAt;

  @Lob
  private String content;

  @Transient
  private List<BaseTag> tags;
  private String signature;

  public EventEntity(String eventIdString, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this.eventIdString = eventIdString;
    this.kind = kind;
    this.pubKey = pubKey;
    this.createdAt = createdAt;
    this.signature = signature;
    this.content = content;
  }

  public EventEntity(long id, String eventIdString, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this(eventIdString, kind, pubKey, createdAt, signature, content);
    this.id = id;
  }
}
