/**
 *
 */
package one.tracking.framework.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import one.tracking.framework.entity.meta.container.Container;

/**
 * @author Marko Voß
 *
 */
public interface ContainerRepository extends CrudRepository<Container, Long> {

  List<Container> findByQuestionsId(Long questionId);
}
