/**
 *
 */
package one.tracking.framework.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;
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
@NamedQueries({
    @NamedQuery(name = "ParticipantImport.findById",
        query = "SELECT i FROM ParticipantImport i WHERE i.id = ?1"),
    @NamedQuery(name = "ParticipantImport.findByIdAndStatus",
        query = "SELECT i FROM ParticipantImport i WHERE i.id = ?1 AND i.status = ?2")
})
public class ParticipantImport {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  @Column(unique = true, nullable = false, length = 36)
  private String id;

  @Min(0)
  @Column(nullable = false)
  private int countFailed;

  @Min(0)
  @Column(nullable = false)
  private int countSkipped;

  @Min(0)
  @Column(nullable = false)
  private int countSuccess;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false, updatable = false)
  private String createdBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ParticipantImportStatus status;

  @PrePersist
  void onPrePersist() {
    if (this.id == null) {
      setCreatedAt(Instant.now());
    }
  }
}
