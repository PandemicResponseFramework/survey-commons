/**
 *
 */
package one.tracking.framework.entity.health;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import one.tracking.framework.entity.User;

/**
 * @author Marko Voß
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "startTime", "endTime"})
    })
@NamedQueries({@NamedQuery(name = "StepCount.findByCreatedAtBetween",
    query = "SELECT sc FROM StepCount sc WHERE "
        + "(sc.updatedAt IS NULL AND sc.createdAt >= ?1 AND sc.createdAt <= ?2) OR "
        + "(sc.updatedAt IS NOT NULL AND sc.updatedAt >= ?1 AND sc.updatedAt <= ?2) "
        + "ORDER BY sc.id ASC")})
public class StepCount {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(optional = false)
  private User user;

  @Column(nullable = false)
  private Integer stepCount;

  @Column(nullable = false)
  private Instant startTime;

  @Column(nullable = false)
  private Instant endTime;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = true, updatable = true)
  private Instant updatedAt;

  @Version
  private Integer version;

  @PrePersist
  void onPrePersist() {
    if (this.id == null) {
      setCreatedAt(Instant.now());
    }
  }
}
