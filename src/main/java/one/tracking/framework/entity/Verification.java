/**
 *
 */
package one.tracking.framework.entity;

import static one.tracking.framework.entity.DataConstants.TOKEN_VERIFY_LENGTH;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Marko Voß
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(name = "IDX_EMAIL", columnList = "email"),
    @Index(name = "IDX_HASH", columnList = "hash"),
})
@NamedQueries({
    @NamedQuery(name = "Verification.existsByHash",
        query = "SELECT DISTINCT true FROM Verification v WHERE v.hash = ?1"),
    @NamedQuery(name = "Verification.existsByImportId",
        query = "SELECT DISTINCT true FROM Verification v WHERE v.participantImport.id = ?1"),
    @NamedQuery(name = "Verification.findByEmail",
        query = "SELECT v FROM Verification v WHERE v.email = ?1"),
    @NamedQuery(name = "Verification.findByImportIdOrderByCreatedAtAsc",
        query = "SELECT v FROM Verification v WHERE v.participantImport.id = ?1 ORDER BY v.createdAt ASC"),
    @NamedQuery(name = "Verification.findByCreatedAtBeforeOrderByEmailAsc",
        query = "SELECT v FROM Verification v WHERE v.createdAt <= ?1 ORDER BY v.email ASC"),
    @NamedQuery(name = "Verification.deleteByEmail",
        query = "DELETE FROM Verification v WHERE v.email = ?1"),
})
public class Verification {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, length = 256, unique = true)
  private String email;

  @Column(nullable = false, length = TOKEN_VERIFY_LENGTH, unique = true)
  private String hash;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VerificationState state;

  @ManyToOne(optional = true)
  private ParticipantImport participantImport;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false, updatable = false)
  private String createdBy;

  @Column(nullable = true, updatable = true)
  private Instant updatedAt;

  @Column(nullable = true, updatable = true)
  private String updatedBy;

  @PrePersist
  void onPrePersist() {
    if (this.id == null) {
      setCreatedAt(Instant.now());
    }
  }
}
