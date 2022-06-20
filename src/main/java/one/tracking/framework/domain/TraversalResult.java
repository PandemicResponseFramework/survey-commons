/**
 *
 */
package one.tracking.framework.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import one.tracking.framework.entity.meta.container.Container;
import one.tracking.framework.entity.meta.question.Question;

/**
 * @author Marko Voß
 *
 */
@Data
public class TraversalResult {

  private final List<Question> consumedQuestions = new ArrayList<>();
  private final List<Container> consumedContainers = new ArrayList<>();

  public void merge(final TraversalResult other) {

    if (other == null)
      return;

    this.consumedQuestions.addAll(other.getConsumedQuestions());
    this.consumedContainers.addAll(other.getConsumedContainers());
  }

  public void add(final Question question) {
    this.consumedQuestions.add(question);
  }

  public void add(final Container container) {
    this.consumedContainers.add(container);
  }
}
