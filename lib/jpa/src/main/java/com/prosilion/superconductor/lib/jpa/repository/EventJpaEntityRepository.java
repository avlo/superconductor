package com.prosilion.superconductor.lib.jpa.repository;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface EventJpaEntityRepository extends JpaRepository<EventJpaEntity, Long> {
  Sort DESC_SORT_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

  List<EventJpaEntity> findAll(@NonNull Sort sort);
  Optional<EventJpaEntityIF> findByUid(@NonNull Long uid);
  Optional<EventJpaEntityIF> findByEventId(String eventId);
  List<EventJpaEntityIF> findByPubKey(@NonNull String pubKey, Sort sort);
  List<EventJpaEntityIF> findByKind(@NonNull Integer kind, Sort sort);

//  TODO: below is properly structured query
//  SELECT * from EVENT e, PUBKEY_TAG pt, "event-pubkey_tag-join" eptjoin, address_tag at, "event-address_tag-join" atjoin where e.kind = :kind and at.uuid = :uuid and pt.public_key = :public_key and e.event_id = eptjoin.event_id and e.event_id = atjoin.event_id;

//  TODO: @Query format reference  
//  @Query(value = "SELECT new EventJpaEntity(c.id, o.id, p.id, c.name, c.email, o.orderDate, p.productName, p.price) "
//      + " from Customer c, CustomerOrder o ,Product p "
//      + " where c.id=o.customer.id "
//      + " and o.id=p.customerOrder.id "
//      + " and c.id=?1 ")  
//  List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(@NonNull Kind kind, @NonNull PublicKey referencePubKeyTag, @NonNull AddressTag addressTag, Sort sort);

  default @NonNull List<EventJpaEntity> findAll() {
    return findAll(DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventJpaEntityIF> findByKind(@NonNull Integer kind) {
    return findByKind(kind, DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventJpaEntityIF> findByPubKey(@NonNull String pubKey) {
    return findByPubKey(pubKey, DESC_SORT_CREATED_AT);
  }

  default @NonNull List<EventJpaEntityIF> getEventsByKindAndPubKeyTag(@NonNull Kind kind, @NonNull PublicKey referencePubKeyTag) {
//    return getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag, DESC_SORT_CREATED_AT);
    return findByKind(kind.getValue());
  }

  default @NonNull List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(@NonNull Kind kind, @NonNull PublicKey referencePubKeyTag, @NonNull AddressTag addressTag) {
//    return getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag, DESC_SORT_CREATED_AT);
    return findByKind(kind.getValue());
  }

  default @NonNull List<EventJpaEntityIF> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    assert (false);
    return findByKind(kind.getValue());
  }

  default @NonNull List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag) {
    assert (false);
    return findByKind(kind.getValue());
  }
}
