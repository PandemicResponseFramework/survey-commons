/**
 *
 */
package one.tracking.framework.repo;

import org.springframework.data.repository.CrudRepository;
import one.tracking.framework.entity.meta.question.Question;

/**
 * @author Marko Voß
 *
 */
public interface QuestionRepository extends CrudRepository<Question, Long> {

}
