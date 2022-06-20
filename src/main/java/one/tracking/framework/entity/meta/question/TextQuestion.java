/**
 *
 */
package one.tracking.framework.entity.meta.question;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Marko Voß
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("TEXT")
public class TextQuestion extends Question {

  @Column(nullable = false)
  private Boolean multiline;

  @Column(nullable = false)
  private Integer length;

  @Override
  public QuestionType getType() {
    return QuestionType.TEXT;
  }

  @Override
  @PrePersist
  void onPrePersist() {
    super.onPrePersist();
  }
}
