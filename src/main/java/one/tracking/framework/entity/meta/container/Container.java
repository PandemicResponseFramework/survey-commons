/**
 *
 */
package one.tracking.framework.entity.meta.container;

import java.time.Instant;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import one.tracking.framework.entity.meta.ReleaseStatusType;
import one.tracking.framework.entity.meta.question.Question;

/**
 * @author Marko Voß
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CONTAINER_TYPE", discriminatorType = DiscriminatorType.STRING, length = 9)
@Entity
public class Container {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToMany(fetch = FetchType.LAZY)
  @OrderBy("ranking ASC")
  private List<Question> questions;

  @ManyToOne(fetch = FetchType.EAGER)
  private Question parent;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  protected void onPrePersist() {
    if (this.id == null) {
      setCreatedAt(Instant.now());
    }
  }

  @PreRemove
  protected void onPreRemove() {
    this.questions.removeIf(p -> p.getReleaseStatus() == ReleaseStatusType.EDIT);
  }
}
