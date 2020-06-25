/**
 *
 */
package one.tracking.framework.repo;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import one.tracking.framework.entity.Verification;
import one.tracking.framework.entity.VerificationState;

/**
 * @author Marko Voß
 *
 */
public interface VerificationRepository extends CrudRepository<Verification, Long> {

  Optional<Verification> findByHashAndState(String hash, VerificationState state);

  Optional<Verification> findByEmail(String email);

  boolean existsByHash(String hash);
}
